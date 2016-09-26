import app.Application;
import app.applicationimpl.Client;
import app.applicationimpl.Proxy;
import app.applicationimpl.Server;

import org.apache.commons.cli.ParseException;
import util.ByteUtil;
import util.Config;

import java.io.IOException;
import java.util.Arrays;

/**
 * Entry point into the application is the {@link tsapp} class that will take the passed in
 * arguments and setup an {@link Application} and run it.
 * <p>
 * Created by nathaniel on 8/24/16.
 */
public class tsapp {

    /**
     * Starts up the required {@link Application} based off of the passed in commands.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            Config c = null;
            c = Config.parseArgs(args);


            Application app = null;
            if (c.isClient()) {
                app = new Client();
            } else if (c.isProxy()) {
                app = new Proxy();
            } else if (c.isServer()) {
                app = new Server();
            }
            if (app != null) {
                app.setConfig(c);
                Application finalApp = app;
                Thread t = new Thread(() -> {
                    try {
                        finalApp.run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                t.setDaemon(false);
                t.start();
            } else {
                System.err.println("Unable to select the running environment.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}