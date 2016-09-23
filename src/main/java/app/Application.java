package app;


import util.Config;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * Created by nathaniel on 8/25/16.
 */
public interface Application {

    void setConfig(Config c);

    void run() throws IOException;

}
