package model;

import util.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Optional;

/**
 * A {@link Message} can contain multiple fields, but it is import to differentiate them.
 * This enum assigns values to particular fields so that they can be easily identified.
 * <p>
 * Created by nathaniel on 9/14/16.
 */
public enum FieldType {
    UNKNOWN(0, byte[].class),
    USER(1, String.class),
    PASSWORD(2, String.class),
    TIME(3, long.class),
    IPPORTSTACK(4, String[].class),
    TIMESTACK(5, long[].class),
    STATUS(6, int.class);

    // public final variables
    public final Class<?> clazz;
    public final int id;

    FieldType(int id, Class<?> aClass) {
        this.id = id;
        this.clazz = aClass;
    }

    /**
     * Allows for the lookup of a {@link FieldType} based off of a string.
     *
     * @param name
     * @return FieldType
     */
    public static FieldType lookup(String name) {
        return FieldType.valueOf(name.toUpperCase());
    }

    /**
     * Allows for the lookup of a {@link FieldType} based off of an int.
     *
     * @param id
     * @return FieldType
     */
    public static FieldType lookup(int id) {
        Optional<FieldType> optional = Arrays.stream(FieldType.values()).filter((ft) -> ft.id == id).findFirst();
        if (optional.isPresent())
            return optional.get();
        throw new RuntimeException("Cannot find fieldtype");
    }

    /**
     * Since {@link Message}s need to be converted into bytes, there {@link Field}s also need to
     * be turned into bytes. This method provides an easy way to get the {@link Field} identifiers
     * byte array.
     *
     * @param name
     * @return byte[]
     */
    public static byte[] lookupByteArrayIdentifier(String name) {
        try {
            return ByteUtil.getBytes(lookup(name).id);
        } catch (IllegalArgumentException e) {
            // unknown
            return ByteUtil.combine(ByteUtil.getBytes(FieldType.UNKNOWN.id), ByteUtil.getBytes(name.length()), name.getBytes());
        }
    }

    /**
     * This method allows for a Field to be constructed based off of an unknown field.
     *
     * @param byteArray
     * @return Field
     */
    public static Field parseUnknown(byte[] byteArray) {
        Field ret = new Field();
        int length = ByteUtil.getInt(byteArray, 0);
        ret.setId(ByteUtil.getString(byteArray, 4, Math.min(byteArray.length, 4 + length)));
        return ret;
    }
}
