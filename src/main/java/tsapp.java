import app.Application;
import app.applicationimpl.Client;
import app.applicationimpl.Proxy;
import app.applicationimpl.Server;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.InetAddressOptionHandler;
import util.ByteUtil;
import util.Config;

import java.util.Arrays;

/**
 * Created by nathaniel on 8/24/16.
 */
public class tsapp {

    public static void main(String[] args){

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
        if(app != null) {
            app.setConfig(c);
            app.run();
        }else{
            System.err.println("Unable to select the running environment.");
        }
    }

}
