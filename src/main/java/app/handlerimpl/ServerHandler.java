package app.handlerimpl;

import app.Application;
import app.Handler;
import model.Message;
import util.MessageHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.Socket;

/**
 * Created by nathaniel on 8/25/16.
 */
public class ServerHandler implements Handler{

    private final Application app;

    public ServerHandler(Application app){
        this.app = app;
    }


    @Override
    public void handle(Socket socket) {
        try {
            InputStream is = socket.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while((length = is.read(buffer)) < 0){
                baos.write(buffer,0,length);
            }
            Message message = MessageHandler.getMessage(baos.toByteArray());
            // handle the particular message

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(DatagramPacket packet) {
        byte[] data = packet.getData();
        Message message = MessageHandler.getMessage(data);
        // handler the particular message
        Message outgoing = incomeingToOutgoing(message);

    }

    private Message incomeingToOutgoing(Message message){
        Message ret = new Message();
        return ret;
    }
}
