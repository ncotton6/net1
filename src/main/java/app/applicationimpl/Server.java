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

import javax.xml.crypto.Data;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
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

    public void setConfig(Config c) {
        this.config = c;
        this.time = new AtomicLong(c.getTime());
        this.username = c.getUser();
        this.password = c.getPass();
    }

    public void run() {
        try {
            int udpPort = Integer.valueOf(config.getArguments().get(0));
            int tcpPort = Integer.valueOf(config.getArguments().get(1));

            final Handler handler = new ServerHandler(this);
            // start up the udp port
            final DatagramSocket udpSocket = new DatagramSocket(udpPort);
            Thread udpThread = new Thread(() -> {
                while (!stop) {
                    try {
                        byte[] receiveData = new byte[2048];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        udpSocket.receive(receivePacket);
                        // handle code
                        Thread t = new Thread(() -> {
                            System.out.println("Server: Received Message");
                            byte[] recvBytes = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
                            Message m = MessageHandler.getMessage(recvBytes);
                            System.out.println(m.getOp());
                            Message toSend = respond(m);
                            if (toSend != null) {
                                System.out.println("Server: Sending Message");
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
            });
            udpThread.setDaemon(true);
            udpThread.start();

            // start up the tcp port
            final ServerSocket tcpSocket = new ServerSocket(tcpPort);
            Thread tcpThread = new Thread(() -> {
                while (!stop) {
                    try {
                        Socket acceptedSocket = tcpSocket.accept();
                        // handle code
                        Thread t = new Thread(() -> {
                            try {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                InputStream is = acceptedSocket.getInputStream();
                                byte[] buffer = new byte[2048];
                                int read = 0;
                                while ((read = is.read(buffer)) != -1) {
                                    baos.write(buffer, 0, read);
                                }
                                Message recv = MessageHandler.getMessage(baos.toByteArray());
                                Message toSend = respond(recv);
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
            });
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
            Message ret = new Message((byte) 1, Operation.GETTIMERETURN);
            ret.addField(new Field(FieldType.TIME, time.get()));
            return ret;
        } else if (recv.getOp() == Operation.CHANGETIME) {
            System.out.println("Server: Operation CHANGETIME");
            boolean changed = false;
            Object username = recv.get(FieldType.USER);
            Object password = recv.get(FieldType.PASSWORD);
            Object time = recv.get(FieldType.TIME);
            if (this.username.equals(username) && this.password.equals(password) && time != null && time instanceof Long) {
                this.time.set((long) time);
                changed = true;
            }
            Message ret = new Message((byte) 1, Operation.CHANGETIMERETURN);
            ret.addField(new Field(FieldType.STATUS, changed));
            return ret;
        }
        return null;
    }

    @Override
    public DatagramSocket getDatagramSocket() {
        return null;
    }

    @Override
    public ServerSocket getServerSocket() {
        return null;
    }
}
