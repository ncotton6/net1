package app.applicationimpl;

import app.Application;
import model.Field;
import model.FieldType;
import model.Message;
import model.Operation;
import util.Config;
import util.MessageHandler;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by nathaniel on 8/25/16.
 */
public class Client implements Application {

    private Config config;
    private DatagramSocket ds;
    private Socket s;

    public void setConfig(Config c) {
        this.config = c;
    }

    public void run() throws IOException {
        for (int i = 0; i < Math.max(1, config.getNumberOfTimes()); ++i) {
            if (config.getTime() > 0 && config.getUser() != null && config.getPass() != null) {
                System.out.println("Client: Selected Operation is to CHANGETIME");
                // run to set the time
                Message setTime = new Message((byte) 1, Operation.CHANGETIME);
                setTime.addField(new Field(FieldType.TIME, config.getTime()))
                        .addField(new Field(FieldType.USER, config.getUser()))
                        .addField(new Field(FieldType.PASSWORD, config.getPass()));
                send(setTime);
                Message recv = recv();
            } else {
                // run to get the time
                System.out.println("Client: Selected Operation is to GETTIME");
                Message getTime = new Message((byte)1,Operation.GETTIME);
                send(getTime);
                Message m = recv();
                System.out.println(m.get(FieldType.TIME));
            }

            if (s != null && !s.isClosed())
                s.close();

            if (ds != null && !ds.isClosed())
                ds.close();
        }
    }

    private void send(Message message) throws IOException {
        System.out.println("Client: Sending Message");
        byte[] bytes = message.getByteArray();
        if (config.isUseTCP()) {
            s = new Socket(config.getServerAddress(), config.getPort());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.write(bytes);
            out.flush();
        } else {
            System.out.println("Client Bytes: " + Arrays.toString(bytes));
            ds = new DatagramSocket(0);
            DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
            dp.setAddress(InetAddress.getByName(config.getServerAddress()));
            dp.setPort(config.getPort());
            ds.send(dp);
        }
        System.out.println("Client: Message Sent");
    }

    private Message recv() throws IOException {
        System.out.println("Client: Receiving Message");
        if(config.isUseTCP()){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = s.getInputStream();
            byte[] buffer = new byte[2048];
            int read = 0;
            while((read = is.read(buffer)) != -1){
                baos.write(buffer,0,read);
            }
            System.out.println("Client: Message Received");
            return MessageHandler.getMessage(baos.toByteArray());
        }else{
            DatagramPacket dp = new DatagramPacket(new byte[2048],2048);
            ds.receive(dp);
            System.out.println("Client: Message Received");
            return MessageHandler.getMessage(Arrays.copyOf(dp.getData(),dp.getLength()));
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
}
