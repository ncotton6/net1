package app.applicationimpl;


import app.Application;
import model.Field;
import model.FieldType;
import model.Message;
import model.Operation;
import util.Config;
import util.MessageHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nathaniel on 8/25/16.
 */
public class Proxy implements Application {
    private Config config;
    private boolean stop = false;
    private DatagramSocket udpSocket;
    private ServerSocket tcpSocket;
    private Map<String, Socket> addressToSocket = new HashMap<>();

    public void setConfig(Config c) {
        this.config = c;
    }

    public void run() {
        try {

            int udpPort = config.getPort();
            int tcpPort = config.getSecondPort();
            udpSocket = new DatagramSocket(udpPort);
            tcpSocket = new ServerSocket(tcpPort);

            Thread udpThread = getUDPServerThread();
            udpThread.setDaemon(false);
            udpThread.start();


            Thread tcpThread = getTCPServerThread();
            tcpThread.setDaemon(false);
            tcpThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Thread getUDPServerThread() {
        return new Thread(() -> {
            while (!stop) {
                try {
                    byte[] buffer = new byte[2048];
                    DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                    udpSocket.receive(dp);
                    System.out.println("Proxy: Received UDP Message");
                    Thread t = new Thread(() -> {
                        byte[] bytes = Arrays.copyOf(dp.getData(), dp.getLength());
                        Message recv = MessageHandler.getMessage(bytes);
                        if (config.isUseTCP()) {
                            System.out.println("Proxy: Forwarding with TCP");
                            Message sendBack = forwardTCP(recv);
                            System.out.println("Proxy: Forwarding with UDP");
                            byte[] toSend = sendBack.getByteArray();
                            DatagramPacket dpSend = new DatagramPacket(toSend, toSend.length);
                            dpSend.setAddress(dp.getAddress());
                            dpSend.setPort(dp.getPort());
                            try {
                                udpSocket.send(dpSend);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Proxy: Forwarding with UDP");
                            forwardUDP(recv);
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            udpSocket.close();
        });
    }

    private Thread getTCPServerThread() {
        return new Thread(() -> {
            while (!stop) {
                try {
                    Socket s = tcpSocket.accept();
                    System.out.println("Proxy: Receiving TCP Message");
                    Thread t = new Thread(() -> {
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            InputStream is = s.getInputStream();
                            int read = 0;
                            byte[] buffer = new byte[2048];
                            while ((read = is.read(buffer)) >= 0) {
                                if (read != 0)
                                    baos.write(buffer, 0, read);
                            }
                            Message m = MessageHandler.getMessage(baos.toByteArray());
                            if (config.isUseUDP()) {
                                String[] ipportstack = (String[]) m.get(FieldType.IPPORTSTACK);
                                String address = ipportstack[ipportstack.length - 1];
                                addressToSocket.put(address, s);
                                System.out.println("Proxy: Forwarding with UDP");
                                forwardUDP(m);
                            } else {
                                System.out.println("Proxy: Forwarding with TCP");
                                Message toSend = forwardTCP(m);
                                s.getOutputStream().write(toSend.getByteArray());
                                s.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void forwardUDP(Message m) {
        if (m.getOp() == Operation.CHANGETIME || m.getOp() == Operation.GETTIME) {
            // going
            addTransportInfo(m, udpSocket.getLocalAddress().getHostAddress(), udpSocket.getLocalPort());
            byte[] bytes = m.getByteArray();
            DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
            try {
                dp.setAddress(InetAddress.getByName(config.getServerAddress()));
                dp.setPort(config.getProxyUDP());
                udpSocket.send(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // coming could need to be transmitted via TCP
            updateTransportInfo(m, udpSocket.getLocalAddress().getHostAddress(), udpSocket.getLocalPort());
            String addressport = findGoingTo(m, udpSocket.getLocalAddress().getHostAddress(), udpSocket.getLocalPort());
            if (addressToSocket.containsKey(addressport)) {
                Socket s = addressToSocket.get(addressport);
                addressToSocket.remove(addressport);
                try {
                    s.getOutputStream().write(m.getByteArray());
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                String[] goingto = addressport.split(":");

                try {
                    InetAddress address = InetAddress.getByName(goingto[0]);
                    int port = Integer.valueOf(goingto[1]);
                    byte[] bytes = m.getByteArray();
                    DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
                    dp.setAddress(address);
                    dp.setPort(port);
                    udpSocket.send(dp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private Message forwardTCP(Message m) {
        Message ret = null;
        addTransportInfo(m, tcpSocket.getInetAddress().getHostAddress(), tcpSocket.getLocalPort());
        try {
            Socket s = new Socket(config.getServerAddress(), config.getProxyTCP());
            s.getOutputStream().write(m.getByteArray());
            s.shutdownOutput();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int read = 0;
            while ((read = s.getInputStream().read(buffer)) >= 0) {
                if (read != 0)
                    baos.write(buffer, 0, read);
            }
            ret = MessageHandler.getMessage(baos.toByteArray());
            updateTransportInfo(ret, tcpSocket.getInetAddress().getHostAddress(), tcpSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void addTransportInfo(Message m, String address, int port) {
        String[] ipportstack = (String[]) m.get(FieldType.IPPORTSTACK);
        long[] timestack = (long[]) m.get(FieldType.TIMESTACK);
        ipportstack = Arrays.copyOf(ipportstack, ipportstack.length + 1);
        ipportstack[ipportstack.length - 1] = String.format("%s:%s", address, port);
        timestack = Arrays.copyOf(timestack, timestack.length + 1);
        timestack[timestack.length - 1] = System.currentTimeMillis();
        m.addField(new Field(FieldType.IPPORTSTACK, ipportstack));
        m.addField(new Field(FieldType.TIMESTACK, timestack));
    }

    private void updateTransportInfo(Message m, String address, int port) {
        String[] ipportstack = (String[]) m.get(FieldType.IPPORTSTACK);
        long[] timestack = (long[]) m.get(FieldType.TIMESTACK);
        String search = String.format("%s:%s", address, port);
        for (int i = 0; i < ipportstack.length; ++i) {
            if (search.equals(ipportstack[i])) {
                timestack[i] = System.currentTimeMillis() - timestack[i];
            }
        }
    }

    private String findGoingTo(Message m, String address, int port) {
        String[] ipportstack = (String[]) m.get(FieldType.IPPORTSTACK);
        String search = String.format("%s:%s", address, port);
        for (int i = 0; i < ipportstack.length; ++i) {
            if (search.equals(ipportstack[i])) {
                return ipportstack[i - 1];
            }
        }
        throw new RuntimeException("Cannot find where to go next");
    }
}
