import controller.Application;
import controller.Client;
import controller.Proxy;
import controller.Server;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import util.Config;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by nathaniel on 8/24/16.
 */
public class tsapp {

    public static void main(String[] args){
        System.out.println("Run");

        Config c = new Config();

        CmdLineParser parser = new CmdLineParser(c);
        try{
            parser.parseArgument(args);


            if(false){ // todo add additional checks to the arguments parsing
                throw new CmdLineException("The provided arguments do not specify the proper connection information.");
            }

        }catch (CmdLineException e){
            System.err.println(e.getMessage());
            System.err.println("java tsapp -{c,s,p} [options] [server address] port [2nd port]");
            parser.printUsage(System.err);
            return; // end the program
        }

        Application app = null;
        if(c.isClient()){
            app = new Client();
        }else if(c.isProxy()){
            app = new Proxy();
        }else if(c.isServer()){
            app = new Server();
        }

        app.setConfig(c);
        app.run();
    }

}
