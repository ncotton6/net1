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

    private int version = 0;
    private Operation op;
    private Map<Object, ByteArray> fields = new HashMap<>();

    public Message() {
    }

    public Message(int version, Operation op, Map<Object, ByteArray> fields) {
        this.version = version;
        this.op = op;
        this.fields = fields;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Operation getOp() {
        return op;
    }

    public void setOp(Operation op) {
        this.op = op;
    }

    public Map<Object, ByteArray> getFields() {
        return fields;
    }

    public void setFields(Map<Object, ByteArray> fields) {
        this.fields = fields;
    }

    public void addField(Field f) {
        if (fields == null)
            fields = new HashMap<>();
        fields.put(f.getId(), f);
    }

    @Override
    public byte[] getByteArray() {
        if (version != 0) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (ByteArray ba : fields.values()) {
                    baos.write(ba.getByteArray());
                }
                byte[] version = ByteUtil.getBytes(this.version);
                byte[] operation = ByteUtil.getBytes(op.id);
                ByteArrayOutputStream ret = new ByteArrayOutputStream();
                ret.write(version);
                ret.write(operation);
                baos.writeTo(ret);
                ret.flush();
                return ret.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }
        throw new RuntimeException("Version Must Be Set");
    }
}
