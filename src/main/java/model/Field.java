package model;

import util.ByteUtil;

/**
 * Created by nathaniel on 8/29/16.
 */
public class Field implements ByteArray {

    private int id;
    private byte[] data;

    public Field() {
    }

    public Field(int id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    public Field(String name, byte[] data) {
        this.id = translateName(name);
        this.data = data;
    }

    public Field(int id, String data) {
        this.id = id;
        this.data = data.getBytes();
    }

    public Field(String name, String data) {
        this.id = translateName(name);
        this.data = data.getBytes();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public String getDataAsString(){
        return new String(data);
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] getByteArray() {
        byte[] byteId = ByteUtil.getBytes(id);
        byte[] length = ByteUtil.getBytes(data.length);
        byte[] ret = new byte[data.length + byteId.length + length.length];
        System.arraycopy(byteId, 0, ret, 0, byteId.length);
        System.arraycopy(length, 0, ret, byteId.length, length.length);
        System.arraycopy(data, 0, ret, byteId.length + length.length, data.length);
        return ret;
    }

    private int translateName(String name) {
        return FieldMapper.getId(name);
    }
}
