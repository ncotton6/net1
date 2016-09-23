package util;

import model.Field;
import model.FieldType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by nathaniel on 8/29/16.
 */
public class ByteUtil {

    public static byte[] getBytes(int num) {
        return ByteBuffer.allocate(4).putInt(num).array();
    }

    public static byte[] getBytes(byte num) {
        return new byte[]{num};
    }

    public static byte[] getBytes(short num) {
        return ByteBuffer.allocate(2).putShort(num).array();
    }

    private static byte[] getBytes(long num) {
        return ByteBuffer.allocate(8).putLong(num).array();
    }

    public static byte[] getBytes(long[] array){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for(int i = 0; i < array.length; ++i){
            try {
                baos.write(getBytes(array[i]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }

    public static int getInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static int getInt(byte[] bytes, int i) {
        return getInt(Arrays.copyOfRange(bytes, i, i + 4));
    }

    public static short getShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getShort();
    }

    public static short getShort(byte[] bytes, int i) {
        return getShort(Arrays.copyOfRange(bytes, i, i + 2));
    }

    public static long getLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static long getLong(byte[] bytes, int index) {
        return getLong(Arrays.copyOfRange(bytes, index, index + 8));
    }

    public static byte getByte(byte[] bytes) {
        return bytes[0];
    }

    public static String getString(byte[] bytes, int start, int end) {
        byte[] stringBytes = Arrays.copyOfRange(bytes, start, end);
        return new String(stringBytes);
    }

    public static byte[] combine(byte[]... arrays) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            for (byte[] bytes : arrays) {
                baos.write(bytes);
            }
            byte[] ret = baos.toByteArray();
            baos.close();
            return ret;
        } catch (IOException e) {
            // something went wrong, reverting to a different method.
            int size = 0;
            for (byte[] bytes : arrays) {
                size += bytes.length;
            }
            int index = 0;
            byte[] ret = new byte[size];
            for (byte[] bytes : arrays) {
                System.arraycopy(bytes, 0, ret, index, bytes.length);
                index += bytes.length;
            }
            return ret;
        }

    }

    public static byte[] convert(String name, Object objData) throws IOException {
        if (objData instanceof byte[]) {
            byte[] bytes = (byte[]) objData;
            bytes = combine(getBytes(bytes.length), bytes);
            if (name == null)
                return bytes;
            return ByteUtil.combine(FieldType.lookupByteArrayIdentifier(name), bytes);
        }else if(objData instanceof long[]){
            byte[] bytes = getBytes((long[])objData);
            bytes = combine(getBytes(bytes.length),bytes);
            if(name == null)
                return bytes;
            return combine(FieldType.lookupByteArrayIdentifier(name),bytes);
        }
        else if (objData instanceof String) {
            byte[] bytes = ((String) objData).getBytes();
            bytes = combine(getBytes(bytes.length), bytes);
            if (name == null)
                return bytes;
            return ByteUtil.combine(FieldType.lookupByteArrayIdentifier(name), bytes);
        } else if (objData instanceof Integer) {
            byte[] bytes = ByteUtil.getBytes((int) objData);
            if (name == null)
                return bytes;
            return ByteUtil.combine(FieldType.lookupByteArrayIdentifier(name), ByteUtil.getBytes(bytes.length), bytes);
        } else if (objData instanceof Long) {
            byte[] bytes = getBytes((long) objData);
            if (name == null)
                return bytes;
            return combine(FieldType.lookupByteArrayIdentifier(name), getBytes(bytes.length), bytes);
        } else if (objData instanceof Field)
            return ((Field) objData).getByteArray();
        else if (objData instanceof Object[]) {
            // need to go through all of the objects in the array converting them to a byte array.
            return handleObjectArray(name, (Object[]) objData);
        } else if (objData instanceof Collection) {
            // need to go through all of the objects in the collection converting them to a byte array.
            return handleCollection(name, (Collection) objData);
        } else if (objData instanceof Map) {
            // need to go through all the entries in the map converting them to a byte array.
            return handleMap(name, (Map) objData);
        } else
            throw new RuntimeException("Failed to translate the field to a byte[].");
    }

    private static byte[] handleCollection(String name, Collection objData) throws IOException {
        return handleObjectArray(name, objData.toArray());
    }

    private static byte[] handleMap(String name, Map objData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Set keys = objData.keySet();
        for (Object key : keys) {
            if (key instanceof String) {
                baos.write(convert((String) key, objData.get(key)));
            } else {
                throw new RuntimeException("Maps being converted to bytes need to have strings for keys.");
            }
        }
        byte[] bytes = baos.toByteArray();
        byte[] length = getBytes(bytes.length);
        baos.close();
        if (name != null) {
            byte[] identifier = ByteUtil.getBytes(FieldType.lookup(name).id);
            return ByteUtil.combine(identifier, length, bytes);
        } else {
            return ByteUtil.combine(length, bytes);
        }
    }

    private static byte[] handleObjectArray(String name, Object[] objData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (Object obj : objData) {
            baos.write(convert(null, obj));
        }
        byte[] bytes = baos.toByteArray();
        baos.close();
        byte[] length = getBytes(bytes.length);
        if (name != null) {
            byte[] identifier = ByteUtil.getBytes(FieldType.lookup(name).id);
            return ByteUtil.combine(identifier, length, bytes);
        } else {
            return ByteUtil.combine(length, bytes);
        }
    }
}
