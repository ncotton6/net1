package controller;


import util.Config;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by nathaniel on 8/25/16.
 */
public class Server implements Application {
    private Config config;

    public void setConfig(Config c) {
        this.config = c;
    }

    public void run() {
        try {
            int udpPort = Integer.valueOf(config.getArguments().get(0));
            int tcpPort = Integer.valueOf(config.getArguments().get(1));
            // start up the udp port
            final DatagramSocket udpSocket = new DatagramSocket(udpPort);
            Thread udpThread = new Thread(()->{
                byte[] receiveData = new byte[1024];
                while(true) {
                    DatagramPacket recievePacket = new DatagramPacket(receiveData,receiveData.length);
                    // handle code
                }
            });
            udpThread.setDaemon(true);
            udpThread.start();

            // start up the tcp port
            final ServerSocket tcpSocket = new ServerSocket(tcpPort);
            Thread tcpThread = new Thread(()->{
                while (true){
                    try {
                        Socket acceptedSocket = tcpSocket.accept();
                        // handle code
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            tcpThread.setDaemon(true);
            tcpThread.start();

        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
}
