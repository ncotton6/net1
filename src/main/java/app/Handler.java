package app;

import java.net.DatagramPacket;
import java.net.Socket;

/**
 * Created by nathaniel on 8/25/16.
 */
public interface Handler {

    void handle(Socket socket);
    void handle(DatagramPacket packet);

}
