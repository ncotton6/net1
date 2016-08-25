import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import util.Config;

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
        }catch (CmdLineException e){
            System.err.println(e.getMessage());
            System.err.println("java tsapp -{c,s,p} [options] [server address] port [2nd port]");
            parser.printUsage(System.err);
            return; // end the program
        }

        System.out.println(c.isClient());
    }

}
