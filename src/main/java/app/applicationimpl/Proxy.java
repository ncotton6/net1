package app.applicationimpl;


import app.Application;
import app.Handler;
import app.handlerimpl.ServerHandler;
import model.Message;
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

/**
 * Created by nathaniel on 8/25/16.
 */
public class Proxy implements Application {
    private Config config;
    private boolean stop = false;
    private DatagramSocket udpSocket;
    private ServerSocket tcpSocket;

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

    @Override
    public DatagramSocket getDatagramSocket() {
        return null;
    }

    @Override
    public ServerSocket getServerSocket() {
        return null;
    }

    private Thread getUDPServerThread() {
        return new Thread(() -> {
            while (!stop){
                try{
                    byte[] buffer = new byte[2048];

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            udpSocket.close();
        });
    }

    private Thread getTCPServerThread() {
        return new Thread(() -> {
        });
    }

    private Message forward(Message m, boolean isTCP){
        Message ret = null;
        boolean useTCP = config.isUseTCP();
        if(useTCP){

        }else {

        }
        return ret;
    }
}
