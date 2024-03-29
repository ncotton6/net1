package app.applicationimpl;

import app.Application;
import model.Field;
import model.FieldType;
import model.Message;
import model.Operation;
import util.Config;
import util.MessageHandler;
import util.Timer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * {@link Client} is a component of the {@link Application} that allows for messages to be created and
 * sent across the system.
 * <p>
 * <p>
 * Created by nathaniel on 8/25/16.
 */
public class Client implements Application {

    // Private Variables
    private Config config;
    private DatagramSocket ds;
    private Socket s;

    /**
     * Sets the configuration
     *
     * @param c
     */
    public void setConfig(Config c) {
        this.config = c;
    }

    /**
     * This implementation of run will have the {@link Client} read the passed in
     * {@link Config} object to determine what to do.
     *
     * @throws IOException
     */
    public void run() throws IOException {
        for (int i = 0; i < Math.max(1, config.getNumberOfTimes()); ++i) {
            if (config.getTime() > 0 && config.getUser() != null && config.getPass() != null) {
                System.out.println("Client: Selected Operation is to CHANGETIME");
                // run to set the time
                Message setTime = new Message((byte) 1, Operation.CHANGETIME);
                setTime.addField(new Field(FieldType.TIME, config.getTime()))
                        .addField(new Field(FieldType.USER, config.getUser()))
                        .addField(new Field(FieldType.PASSWORD, config.getPass()))
                        .addField(new Field(FieldType.TIMESTACK, new long[]{System.currentTimeMillis()}));
                Timer t = Timer.start();
                send(setTime);
                Message recv = recv();
                printTransportInfo(recv, t);
            } else {
                // run to get the time
                System.out.println("Client: Selected Operation is to GETTIME");
                Message getTime = new Message((byte) 1, Operation.GETTIME)
                        .addField(new Field(FieldType.TIMESTACK, new long[]{System.currentTimeMillis()}));
                send(getTime);
                Timer t = Timer.start();
                Message m = recv();
                printTransportInfo(m, t);
                long time = (long) m.get(FieldType.TIME);
                if(config.isUseUTCTime()){
                    Date date = new Date();
                    date.setTime(time);
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME,"UTC"));
                    String utc = sdf.format(date);
                    System.out.println("UTC Time: " + utc);
                }else {
                    System.out.println("Time: " + time);
                }
            }

            if (s != null && !s.isClosed())
                s.close();

            if (ds != null && !ds.isClosed())
                ds.close();
        }
    }

    /**
     * Since {@link Message}s returning to the {@link Client} will have transport
     * information on them this method will extract that information and print
     * it out.
     *
     * @param m
     * @param time
     */
    private void printTransportInfo(Message m, Timer time) {
        String[] ipportstack = (String[]) m.get(FieldType.IPPORTSTACK);
        long[] timestack = (long[]) m.get(FieldType.TIMESTACK);
        if (ipportstack != null && timestack != null) {
            timestack[0] = System.currentTimeMillis() - timestack[0];
            long[] timeAtEach = new long[timestack.length];
            for (int i = timestack.length - 2; i >= 0; --i) {
                timeAtEach[i] = timestack[i] - timestack[i + 1];
            }
            System.out.println("------------------------------------------------------");
            System.out.println("-------------  Transport Information  ----------------");
            System.out.println("------------------------------------------------------");
            System.out.println("Number of hops: " + (ipportstack.length - 1));
            System.out.println("Total time: " + time.end() + "ms\n");
            for (int i = 0; i < ipportstack.length; ++i) {
                if (i < timestack.length)
                    System.out.println(String.format("%4s %s (%sms)", "[" + (i) + "]", ipportstack[i], timeAtEach[i]));
                else
                    System.out.println(ipportstack[i]);
            }
            System.out.println("------------------------------------------------------");
        }
    }

    /**
     * For convince this send method was created to take in a {@link Message} as
     * a parameter and read the {@link Config} object to determine how the {@link Message}
     * should be sent.
     *
     * @param message
     * @throws IOException
     */
    private void send(Message message) throws IOException {
        if (config.isUseTCP()) {
            System.out.println("Client: Sending Message over TCP");
            s = new Socket(config.getServerAddress(), config.getPort());
            message.addField(new Field(FieldType.IPPORTSTACK, new String[]{String.format("%s:%s", s.getLocalAddress().getHostAddress(), s.getLocalPort())}));
            byte[] bytes = message.getByteArray();
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.write(bytes);
            out.flush();
            s.shutdownOutput();
        } else {
            System.out.println("Client: Sending Message over UDP");
            ds = new DatagramSocket(0);
            message.addField(new Field(FieldType.IPPORTSTACK, new String[]{String.format("%s:%s", ds.getLocalAddress().getHostAddress(), ds.getLocalPort())}));
            byte[] bytes = message.getByteArray();
            DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
            dp.setAddress(InetAddress.getByName(config.getServerAddress()));
            dp.setPort(config.getPort());
            ds.send(dp);
        }
        System.out.println("Client: Message Sent");
    }

    /**
     * This method will read the {@link Config} object and determine how a {@link Message}
     * should be received.  Then turns the received byte[] into a {@link Message} object.
     *
     *
     * @return
     * @throws IOException
     */
    private Message recv() throws IOException {
        if (config.isUseTCP()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = s.getInputStream();
            byte[] buffer = new byte[2048];
            int read = 0;
            while ((read = is.read(buffer)) >= 0) {
                if (read != 0)
                    baos.write(buffer, 0, read);
            }
            System.out.println("Client: Message Received over TCP");
            return MessageHandler.getMessage(baos.toByteArray());
        } else {
            DatagramPacket dp = new DatagramPacket(new byte[2048], 2048);
            ds.receive(dp);
            System.out.println("Client: Message Received over UDP");
            return MessageHandler.getMessage(Arrays.copyOf(dp.getData(), dp.getLength()));
        }
    }

}
