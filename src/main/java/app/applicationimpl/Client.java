package app.applicationimpl;

import app.Application;
import util.Config;

import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * Created by nathaniel on 8/25/16.
 */
public class Client implements Application {

    private Config config;

    public void setConfig(Config c) {
        this.config = c;
    }

    public void run() {

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
