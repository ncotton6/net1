package util;

import model.Message;
import model.ParseHandler;
import model.VersionOneParseHandler;

/**
 * Created by nathaniel on 8/29/16.
 */
public class MessageHandler {

    public static Message getMessage(byte[] message){
        try {
            byte version = message[0];
            return getParseHandlerForVersion(version).parse(message);
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new RuntimeException("Invalid Message Parse");
    }

    public static ParseHandler getParseHandlerForVersion(byte version){
        if(version == 1){
            return new VersionOneParseHandler();
        }
        throw new RuntimeException("No Such Parser");
    }
}
