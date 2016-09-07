package model;

import util.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nathaniel on 8/29/16.
 */
public class Message implements ByteArray {

    private byte version = 0;
    private Operation op;
    private Map<String, Object> fields = new HashMap<>();

    public Message() {
    }

    public Message(byte version, Operation op, Map<String, Object> fields) {
        this.version = version;
        this.op = op;
        this.fields = fields;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public Operation getOp() {
        return op;
    }

    public void setOp(Operation op) {
        this.op = op;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public void addField(Field f) {
        addData(f.getId(),f);
    }

    public void addData(String name, Object data){
        if (fields == null)
            fields = new HashMap<>();
        fields.put(name, data);
    }

    @Override
    public byte[] getByteArray() {
        if (version != 0) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for(String key : fields.keySet()){
                    baos.write(ByteUtil.convert(key,fields.get(key)));
                }
                byte[] version = ByteUtil.getBytes(this.version);
                byte[] operation = ByteUtil.getBytes(op.id);
                return ByteUtil.combine(version,operation,baos.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }
        throw new RuntimeException("Version Must Be Set");
    }
}
