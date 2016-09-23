package util;

import org.apache.commons.cli.*;

import java.util.*;

/**
 * Created by nathaniel on 8/24/16.
 */
public class Config {

    private boolean client = false;
    private boolean proxy = false;
    private boolean server = false;
    private boolean useUDP = false;
    private boolean useTCP = false;
    private boolean useUTCTime = false;
    private long time = -1;
    private String user = "";
    private String pass = "";
    private int numberOfTimes = 1;
    private int proxyUDP = -1;
    private int proxyTCP = -1;
    private String serverAddress;
    private int port1 = -1;
    private int port2 = -1;

    public boolean isClient() {
        return client;
    }

    public boolean isProxy() {
        return proxy;
    }

    public boolean isServer() {
        return server;
    }

    public boolean isUseUDP() {
        return useUDP;
    }

    public boolean isUseTCP() {
        return useTCP;
    }

    public boolean isUseUTCTime() {
        return useUTCTime;
    }

    public long getTime() {
        return time;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public int getNumberOfTimes() {
        return numberOfTimes;
    }

    public int getProxyUDP() {
        return proxyUDP;
    }

    public int getProxyTCP() {
        return proxyTCP;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getPort() {
        return port1;
    }

    public int getSecondPort() {
        return port2;
    }


    public static Config parseArgs(String[] args) throws ParseException {
        Config c = new Config();
        CommandLineParser clp = new DefaultParser();
        CommandLine cl = clp.parse(getArgsOptions(), args);

        // move command line values to class values
        c.client = cl.hasOption("c");
        c.server = cl.hasOption("s");
        c.proxy = cl.hasOption("p");
        c.useTCP = cl.hasOption("t");
        c.useUDP = cl.hasOption("u") || !cl.hasOption("t");
        c.useUTCTime = cl.hasOption("z");

        c.time = cl.hasOption("T") ? Long.valueOf(cl.getOptionValue("T")) : -1;
        c.numberOfTimes = cl.hasOption("n") ? Integer.valueOf(cl.getOptionValue("n")) : 1;

        c.user = cl.hasOption("user") ? cl.getOptionValue("user") : null;
        c.pass = cl.hasOption("pass") ? cl.getOptionValue("pass") : null;

        c.proxyUDP = cl.hasOption("proxy-udp") ? Integer.valueOf(cl.getOptionValue("proxy-udp")) : -1;
        c.proxyTCP = cl.hasOption("proxy-tcp") ? Integer.valueOf(cl.getOptionValue("proxy-tcp")) : -1;

        if(c.server){
            c.port1 = Integer.valueOf(cl.getArgList().get(0));
            c.port2 = Integer.valueOf(cl.getArgList().get(1));
        }else {
            c.serverAddress = cl.getArgList().get(0);
            c.port1 = Integer.valueOf(cl.getArgList().get(1));
            c.port2 = cl.getArgList().size() >= 3 ? Integer.valueOf(cl.getArgList().get(2)) : -1;
        }

        if(!verifyConfig(c)){
            throw new IllegalArgumentException("Not a valid configuration");
        }
        return c;
    }

    private static boolean verifyConfig(Config c) {
        if(c == null)
            return false; // needs to be an object
        if((c.client && c.proxy) || (c.client && c.server) || (c.proxy && c.server))
            return false; // only one environment may be selected
        return true;
    }

    private static Options getArgsOptions() {
        Options options = new Options();
        // environment
        options.addOption(new Option("c", false, "The application will act as a client"));
        options.addOption(new Option("p", false, "The application will act as a proxy"));
        options.addOption(new Option("s", false, "The application will act as a server"));

        // Transport
        options.addOption(new Option("t", false, "Use TCP for connections"));
        options.addOption(new Option("u", false, "Use UDP for connections"));

        // change the time
        options.addOption(new Option("T", true, "Specifies a time to set"));
        options.addOption(new Option("", "user", true, "Username for the credentials"));
        options.addOption(new Option("", "pass", true, "Password for the credentials"));

        // execution times
        options.addOption(new Option("n", true, "Number of times to execute the command"));

        // time type
        options.addOption(new Option("z", true, "Use UTC time"));

        // proxy transport
        options.addOption(new Option("", "proxy-udp", true, "Proxy UDP connection"));
        options.addOption(new Option("", "proxy-tcp", true, "Proxy TCP connection"));

        return options;
    }


}
