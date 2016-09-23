package app.applicationimpl;


import app.Application;
import app.Handler;
import app.handlerimpl.ServerHandler;
import model.Field;
import model.FieldType;
import model.Message;
import model.Operation;
import util.Config;
import util.MessageHandler;
import util.Timer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by nathaniel on 8/25/16.
 */
public class Server implements Application {
    private Config config;
    private AtomicLong time;
    private String username;
    private String password;
    private boolean stop = false;
    private DatagramSocket udpSocket;
    private ServerSocket tcpSocket;

    public void setConfig(Config c) {
        this.config = c;
        this.time = new AtomicLong(c.getTime());
        this.username = c.getUser();
        this.password = c.getPass();
    }

    public void run() {
        try {
            int udpPort = config.getPort();
            int tcpPort = config.getSecondPort();

            final Handler handler = new ServerHandler(this);
            // start up the udp port
            udpSocket = new DatagramSocket(udpPort);
            Thread udpThread = getUDPServerThread();
            udpThread.setDaemon(false);
            udpThread.start();

            // start up the tcp port
            tcpSocket = new ServerSocket(tcpPort);
            Thread tcpThread = getTCPServerThread();
            tcpThread.setDaemon(false);
            tcpThread.start();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private Message respond(Message recv) {
        System.out.println(recv.getOp());
        if (recv.getOp() == Operation.GETTIME) {
            System.out.println("Server: Operation GETTIME");
            return respondToGETTIME(recv);
        } else if (recv.getOp() == Operation.CHANGETIME) {
            System.out.println("Server: Operation CHANGETIME");
            int changed = 0;
            Object username = recv.get(FieldType.USER);
            Object password = recv.get(FieldType.PASSWORD);
            Object time = recv.get(FieldType.TIME);
            if (this.username.equals(username) && this.password.equals(password) && time != null && time instanceof Long) {
                this.time.set((long) time);
                changed = 1;
            }
            return respondToCHANGETIME(recv, changed);
        }
        return null;
    }

    private Thread getUDPServerThread() {
        return new Thread(() -> {
            while (!stop) {
                try {
                    byte[] receiveData = new byte[2048];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    udpSocket.receive(receivePacket);
                    // handle code
                    Thread t = new Thread(() -> {
                        Timer timer = Timer.start();
                        System.out.println("Server: Received Message");
                        byte[] recvBytes = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
                        Message m = MessageHandler.getMessage(recvBytes);
                        Message toSend = respond(m);
                        toSend = addTransportInformation(toSend, (String[]) m.get(FieldType.IPPORTSTACK),
                                (long[]) m.get(FieldType.TIMESTACK), udpSocket.getLocalAddress().getHostAddress(), udpSocket.getLocalPort(),timer.end());
                        if (toSend != null) {
                            System.out.println(String.format("Server: Sending Message to [%s:%s]",
                                    receivePacket.getAddress().getHostAddress(), receivePacket.getPort()));
                            byte[] bytes = toSend.getByteArray();
                            DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
                            dp.setAddress(receivePacket.getAddress());
                            dp.setPort(receivePacket.getPort());
                            try {
                                udpSocket.send(dp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Server: Message Sent");
                        }

                    });
                    t.setDaemon(true);
                    t.start();
                } catch (IOException e) {
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
                    Socket acceptedSocket = tcpSocket.accept();
                    // handle code
                    Thread t = new Thread(() -> {
                        try {
                            Timer timer = Timer.start();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            InputStream is = acceptedSocket.getInputStream();
                            byte[] buffer = new byte[2048];
                            int read = 0;
                            try {
                                while (is.available() != 0 && (read = is.read(buffer)) > 0) {
                                    baos.write(buffer, 0, read);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Message recv = MessageHandler.getMessage(baos.toByteArray());
                            Message toSend = respond(recv);
                            toSend = addTransportInformation(toSend, (String[]) recv.get(FieldType.IPPORTSTACK),
                                    (long[]) recv.get(FieldType.TIMESTACK), tcpSocket.getInetAddress().getHostAddress(),
                                    tcpSocket.getLocalPort(),timer.end());
                            if (toSend != null) {
                                byte[] bytes = toSend.getByteArray();
                                acceptedSocket.getOutputStream().write(bytes);
                                acceptedSocket.getOutputStream().flush();
                                acceptedSocket.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                tcpSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private Message respondToGETTIME(Message recv) {
        Message ret = new Message((byte) 1, Operation.GETTIMERETURN)
                .addField(new Field(FieldType.TIME, time.get()));
        return ret;
    }

    private Message respondToCHANGETIME(Message recv, int changed) {
        Message ret = new Message((byte) 1, Operation.CHANGETIMERETURN).addField(new Field(FieldType.STATUS, changed));
        return ret;
    }

    private Message addTransportInformation(Message m, String[] ipportstack, long[] timestack, String address, int port, long time) {
        // null checks
        if (ipportstack == null)
            ipportstack = new String[0];
        if (timestack == null)
            timestack = new long[0];

        // take on server time
        ipportstack = Arrays.copyOf(ipportstack, ipportstack.length + 1);
        ipportstack[ipportstack.length - 1] = String.format("%s:%s", address, port);

        m.addField(new Field(FieldType.IPPORTSTACK, ipportstack));

        timestack = Arrays.copyOf(timestack, timestack.length + 1);
        timestack[timestack.length - 1] = time;

        m.addField(new Field(FieldType.TIMESTACK, timestack));

        return m;
    }


}
