package model;

import util.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by nathaniel on 9/14/16.
 */
public enum FieldType {
    UNKNOWN(0, byte[].class),
    USER(1, String.class),
    PASSWORD(2, String.class),
    TIME(3, long.class),
    IPPORTSTACK(4, String[].class),
    TIMESTACK(5,long[].class),
    STATUS(6,int.class);

    public final Class<?> clazz;
    public final int id;

    FieldType(int id, Class<?> aClass) {
        this.id = id;
        this.clazz = aClass;
    }

    public static FieldType lookup(String name) {
        return FieldType.valueOf(name.toUpperCase());
    }

    public static FieldType lookup(int id) {
        Optional<FieldType> optional = Arrays.stream(FieldType.values()).filter((ft) -> ft.id == id).findFirst();
        if (optional.isPresent())
            return optional.get();
        throw new RuntimeException("Cannot find fieldtype");
    }

    public static byte[] lookupByteArrayIdentifier(String name) {
        try {
            return ByteUtil.getBytes(lookup(name).id);
        } catch (IllegalArgumentException e) {
            // unknown
            return ByteUtil.combine(ByteUtil.getBytes(FieldType.UNKNOWN.id), ByteUtil.getBytes(name.length()), name.getBytes());
        }
    }

    public static Field parseUnknown(byte[] byteArray){
        Field ret = new Field();
        int length = ByteUtil.getInt(byteArray,0);
        ret.setId(ByteUtil.getString(byteArray,4,Math.min(byteArray.length,4+length)));
        return ret;
    }
}
