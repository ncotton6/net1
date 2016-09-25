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
 * {@link Proxy} class allows for a some degree of redirection between a client and the origin server.
 * This class will setup hooks similar to those the the origin server setups.  When the proxy receives a
 * message on one of these hooks it will then forward it on to the server.
 * <p>
 * Created by nathaniel on 8/25/16.
 */
public class Proxy implements Application {
    // private variables
    private Config config;
    private boolean stop = false;
    private DatagramSocket udpSocket;
    private ServerSocket tcpSocket;
    private Map<String, Socket> addressToSocket = new HashMap<>();

    /**
     * Sets the {@link Config} object.
     *
     * @param c
     */
    public void setConfig(Config c) {
        this.config = c;
    }

    /**
     * Sets up the hooks for UDP and TCP connections, to process incoming requests.
     */
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

    /**
     * This method produces a {@link Thread} that will handle all incoming UDP requests.
     *
     * @return Thread
     */
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

    /**
     * This method produces a {@link Thread} that will handle all incoming TCP requests.
     *
     * @return
     */
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

    /**
     * For ease of use in other parts of the application this method will accept a {@link Message} object
     * and pass it on to its next destination using UDP.
     * @param m
     */
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

    /**
     * For ease in other parts of the application this method will accept a {@link Message} and
     * forward it along using TCP. Unlike the UDP version of this method this one will return
     * a {@link Message} that was a response from forwarding the TCP message.
     * @param m
     * @return Message
     */
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

    /**
     * This method will add the transport information for this {@link Proxy} to the Message.
     * This method is used for {@link Message}s that are on there way to the origin server.
     *
     * @param m
     * @param address
     * @param port
     */
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

    /**
     * This method will update the transport information stored on a {@link Message}. This
     * method is used for {@link Message}s that are going back to the client.
     *
     * @param m
     * @param address
     * @param port
     */
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

    /**
     * For connections that go from TCP to UDP on there way to the origin server,
     * and need to go back to TCP on there way back to the client. This method
     * proves to be quite useful as it will look at the IPPORTSTACK field and
     * determine what address it the message needs to go to next.  This can be
     * used to lookup the required socket.
     *
     * @param m
     * @param address
     * @param port
     * @return
     */
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
