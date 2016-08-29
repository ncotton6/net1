package app.applicationimpl;


import app.Application;
import util.Config;

/**
 * Created by nathaniel on 8/25/16.
 */
public class Proxy implements Application {
    private Config config;

    public void setConfig(Config c) {
        this.config = c;
    }

    public void run() {

    }
}
