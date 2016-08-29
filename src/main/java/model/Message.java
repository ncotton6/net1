package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nathaniel on 8/29/16.
 */
public class Message implements ByteArray {

    private int version;
    private Operation op;
    private List<ByteArray> data = new ArrayList<>();


    @Override
    public byte[] getByteArray() {
        return new byte[0];
    }
}
