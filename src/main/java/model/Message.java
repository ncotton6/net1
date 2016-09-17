package model;

import util.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by nathaniel on 8/29/16.
 */
public class Message implements ByteArray {

    private byte version = 0;
    private Operation op;
    private Map<String, Object> fields = new HashMap<>();

    public Message() {
    }

    public Message(byte version, Operation op) {
        this.version = version;
        this.op = op;
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

    public Message addField(Field f) {
        return addData(f.getId(),f);
    }

    public Message addData(String name, Object data){
        if (fields == null)
            fields = new HashMap<>();
        fields.put(name, data);
        return this;
    }

    public Object get(FieldType ft) {
        Optional<Object> field = fields.values().stream().filter((v)-> v instanceof Field && ((Field) v).getId().equals(ft.name())).findFirst();
        if(field.isPresent()) {
            if (field.get() instanceof Field)
                return ((Field)field.get()).getObjData();
            return field.get();
        }
        return null;
    }

    @Override
    public byte[] getByteArray() {
        if (version != 0) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(ByteUtil.getBytes(this.version));
                baos.write(ByteUtil.getBytes(op.id));
                for(String key : fields.keySet()){
                    baos.write(ByteUtil.convert(key,fields.get(key)));
                }
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }
        throw new RuntimeException("Version Must Be Set");
    }
}
