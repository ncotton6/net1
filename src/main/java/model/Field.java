package model;

import util.ByteUtil;

import java.io.IOException;

/**
 * Created by nathaniel on 8/29/16.
 */
public class Field implements ByteArray {

    private String id;
    private Object objData;

    public Field() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getObjData() {
        return objData;
    }

    public void setObjData(Object objData) {
        this.objData = objData;
    }

    @Override
    public byte[] getByteArray() {
        try {
            return ByteUtil.convert(id,objData);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert object into byte[].",e);
        }
    }

}