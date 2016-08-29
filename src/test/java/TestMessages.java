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
        m.setVersion(1);
        m.setOp(Operation.GETTIME);
        m.addField(new Field("time","test"));
        byte[] data = m.getByteArray();
        System.out.println(Arrays.toString(data));
        Message m2 = MessageHandler.getMessage(data);
        System.out.println(m2.getVersion());
        System.out.println(m2.getOp());
        for(ByteArray ba : m2.getFields().values()){
            Field f = (Field)ba;
            System.out.println(f.getId() + " : " + Arrays.toString(f.getData()));
        }
    }
}
