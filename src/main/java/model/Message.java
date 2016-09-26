package model;

import util.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * {@link Message} class holds information that is critical for sending information across the network.
 *
 * Created by nathaniel on 8/29/16.
 */
public class Message implements ByteArray {

    // Private Variables
    private byte version = 0;
    private Operation op;
    private Map<String, Object> fields = new HashMap<>();

    // Constructors
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

    // Getters and Setters

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

    /**
     * Adds a {@link Field} to the {@link Message}.
     * @param f
     * @return Message for chaining
     */
    public Message addField(Field f) {
        return addData(f.getId(),f);
    }

    /**
     * Adds data to the {@link Message}.
     * @param name
     * @param data
     * @return
     */
    public Message addData(String name, Object data){
        if (fields == null)
            fields = new HashMap<>();
        fields.put(name, data);
        return this;
    }

    /**
     * Allows for {@link Field} data to be extracted from the {@link Message}.
     * @param ft
     * @return
     */
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

    @Override
    public String toString() {
        return "Message{" +
                "version=" + version +
                ", op=" + op +
                ", fields=" + fields +
                '}';
    }
}
