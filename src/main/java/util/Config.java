package util;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.util.*;

/**
 * Created by nathaniel on 8/24/16.
 */
public class Config {

    @Option(name = "-c", forbids = {"-p", "-s"}, usage = "Designates the application should run as a client.")
    private boolean client = false;

    @Option(name = "-p", forbids = {"-c", "-s"}, usage = "Designates the application should run as a proxy.")
    private boolean proxy = false;

    @Option(name = "-s", forbids = {"-c", "-p"}, usage = "Designates the application should run as a server.")
    private boolean server = false;

    @Option(name = "-u", forbids = {"-s", "-t"}, usage = "use UDP. client & proxy server applications")
    private boolean useUDP = false;

    @Option(name = "-t", forbids = {"-s", "-u"}, usage = "use TCP. client & proxy server applications")
    private boolean tempTest;

    private boolean useTCP = false;

    @Option(name = "-z", depends = {"-c"}, usage = "use UTC time. Client applications.")
    private boolean useUTCTime = false;

    @Option(name = "-T", forbids = {"-p"}, usage = "set server time. client & server applications.")
    private long time = -1;

    @Option(name = "--user", forbids = {"-p"}, usage = "<name>: credentials to use. client & server applications.")
    private String user = "";

    @Option(name = "--pass", forbids = {"-p"}, usage = "<password>: credentials to use. client & server application.")
    private String pass = "";

    @Option(name = "-n", depends = {"-c"}, usage = "<#>: number of consecutive times to query the server. client applications.")
    private int numberOfTimes = 1;

    @Option(name = "-proxy-udp", depends = {"-p"}, usage = "server UDP port to use. proxy application.")
    private int proxyUDP = -1;

    @Option(name = "-proxy-tcp", depends = {"-p"}, usage = "server TCP port to use. proxy application.")
    private int proxyTCP = -1;


    @Argument
    private List<String> arguments = new ArrayList<String>();

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

    public List<String> getArguments() {
        return arguments;
    }

    public String getServerAddress(){
        return arguments.get(0);
    }

    public int getPort(){
        return Integer.valueOf(arguments.get(isServer() ? 0 : 1));
    }

    public int getSecondPort(){
        return Integer.valueOf(arguments.get(isServer() ? 1 : 2));
    }
}
