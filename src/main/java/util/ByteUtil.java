package util;

import model.ByteArray;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by nathaniel on 8/29/16.
 */
public class ByteUtil {

    public static byte[] getBytes(int num){
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(num);
        return bb.array();
    }

    public static int getInt(byte[] bytes){
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb.getInt();
    }

}
