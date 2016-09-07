import model.ByteArray;
import model.Field;
import model.Message;
import model.Operation;
import util.MessageHandler;

import java.util.Arrays;
import java.util.logging.MemoryHandler;

/**
 * Created by nathaniel on 8/29/16.
 */
public class TestMessages {
    public static void main(String[] args) {
        Message m = new Message();
        m.setVersion((byte)1);
        m.setOp(Operation.GETTIME);
        Field f = new Field();
        f.setId("name");
        f.setObjData("test");
        System.out.println(Arrays.toString(f.getByteArray()));
        m.addField(f);
        m.addField(new Field("random","meter"));
        System.out.println(Arrays.toString(m.getByteArray()));
        MessageHandler.getMessage(m.getByteArray());
    }
}
