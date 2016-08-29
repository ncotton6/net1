package model;

/**
 * Created by nathaniel on 8/29/16.
 */
public class Field implements ByteArray {

    private int id;
    private String name;
    private byte[] data;

    @Override
    public byte[] getByteArray() {
        return new byte[0];
    }
}
