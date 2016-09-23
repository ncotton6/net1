import model.*;
import util.ByteUtil;
import util.MessageHandler;

import java.util.Arrays;

/**
 * Created by nathaniel on 9/23/16.
 */
public class Test {


    public static void main(String[] args){
        long[] temp = new long[]{System.currentTimeMillis(),System.currentTimeMillis()};
        Message m = new Message((byte)1, Operation.GETTIME).addField(new Field(FieldType.TIMESTACK,temp));
        byte[] bytes = m.getByteArray();
        Message m2 = MessageHandler.getMessage(bytes);
        long[] temp2 = (long[]) m2.get(FieldType.TIMESTACK);
        System.out.println(Arrays.toString(temp));
        System.out.println(Arrays.toString(temp2));
    }

}
