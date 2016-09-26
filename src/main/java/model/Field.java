package model;

import util.ByteUtil;

import java.io.IOException;

/**
 * {@link Field} class helps to represent the fields that exist within {@link Message}s.
 *
 * Created by nathaniel on 8/29/16.
 */
public class Field implements ByteArray {

    // private variables
    private String id;
    private Object objData;

    // constructors
    public Field() {
    }

    public Field(String id, Object objData) {
        this.id = id;
        this.objData = objData;
    }

    public Field(FieldType ft, Object objData){
        this.id = ft.name();
        this.objData = objData;
    }

    /**
     * Get id
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Set id
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get value
     * @return Object
     */
    public Object getObjData() {
        return objData;
    }

    /**
     * Set object
     * @param objData
     */
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