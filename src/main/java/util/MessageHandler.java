package util;

import model.Message;
import model.ParseHandler;
import model.VersionOneParseHandler;

/**
 * Since there are a good number of messages being passed all around. This {@link MessageHandler} class
 * provides functionality to quickly turn a byte[] into a message.
 *
 * Created by nathaniel on 8/29/16.
 */
public class MessageHandler {

    /**
     * Converts a valid byte array into a {@link Message} object.
     * @param message
     * @return Message
     * @Throws RuntimeException
     */
    public static Message getMessage(byte[] message){
        try {
            byte version = message[0];
            return getParseHandlerForVersion(version).parse(message);
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new RuntimeException("Invalid Message Parse");
    }

    /**
     * Gets the required version of the {@link ParseHandler} to handle the
     * byte[].
     * @param version
     * @return
     */
    public static ParseHandler getParseHandlerForVersion(byte version){
        if(version == 1){
            return new VersionOneParseHandler();
        }
        throw new RuntimeException("No Such Parser");
    }
}
