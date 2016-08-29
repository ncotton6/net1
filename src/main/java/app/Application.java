package app;


import util.Config;

import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * Created by nathaniel on 8/25/16.
 */
public interface Application {

    void setConfig(Config c);

    void run();

    DatagramSocket getDatagramSocket();

    ServerSocket getServerSocket();

}
