package util;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.util.*;

/**
 * Created by nathaniel on 8/24/16.
 */
public class Config {

    @Option(name = "-c",forbids = {"-p","-s"},usage = "Designates the application should run as a client.")
    private boolean client = false;

    @Option(name = "-p", forbids = {"-c","-s"},usage = "Designates the application should run as a proxy.")
    private boolean proxy = false;

    @Option(name = "-s", forbids = {"-c","-p"},usage = "Designates the application should run as a server.")
    private boolean server = false;

    @Option(name = "-u", depends = {"-c", "-p"},forbids = {"-s","-t"},usage = "use UDP. client & proxy server applications")
    private boolean useUDP;

    @Option(name = "-t", depends = {"-c", "-p"},forbids = {"-s","-u"},usage = "use TCP. client & proxy server applications")
    private boolean useTCP;

    @Option(name = "-z", depends = {"-c"}, usage = "use UTC time. Client applications.")
    private boolean useUTCTime;

    @Option(name = "-T", depends = {"-c","-s"},usage = "set server time. client & server applications.")
    private int time;





    @Argument
    private List<String> arguments = new ArrayList<String>();

    public boolean isClient() {
        return client;
    }

    public List<String> getArguments() {
        return arguments;
    }
}
