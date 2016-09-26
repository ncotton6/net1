package util;

import model.Field;
import model.FieldType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * {@link ByteUtil} class has a number of static methods that make working with byte[] easier.
 *
 * Created by nathaniel on 8/29/16.
 */
public class ByteUtil {

    /**
     * Turn an int into a byte array.
     * @param num
     * @return
     */
    public static byte[] getBytes(int num) {
        return ByteBuffer.allocate(4).putInt(num).array();
    }

    /**
     * Turn a byte into a byte array.
     * @param num
     * @return
     */
    public static byte[] getBytes(byte num) {
        return new byte[]{num};
    }

    /**
     * Turn a short into a byte array.
     * @param num
     * @return
     */
    public static byte[] getBytes(short num) {
        return ByteBuffer.allocate(2).putShort(num).array();
    }

    /**
     * Turn a long into a byte array.
     * @param num
     * @return
     */
    private static byte[] getBytes(long num) {
        return ByteBuffer.allocate(8).putLong(num).array();
    }

    /**
     * Turn a long[] into a byte array.
     * @param array
     * @return
     */
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

    /**
     * Get an int from a byte[].
     * @param bytes
     * @return
     */
    public static int getInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    /**
     * Get an int from a byte[].
     * @param bytes
     * @param i
     * @return
     */
    public static int getInt(byte[] bytes, int i) {
        return getInt(Arrays.copyOfRange(bytes, i, i + 4));
    }

    /**
     * Get a short from a byte[].
     * @param bytes
     * @return
     */
    public static short getShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getShort();
    }

    /**
     * Get a short from a byte[].
     * @param bytes
     * @param i
     * @return
     */
    public static short getShort(byte[] bytes, int i) {
        return getShort(Arrays.copyOfRange(bytes, i, i + 2));
    }

    /**
     * Get a long from a byte[].
     * @param bytes
     * @return
     */
    public static long getLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    /**
     * Get a long from a byte[].
     * @param bytes
     * @param index
     * @return
     */
    public static long getLong(byte[] bytes, int index) {
        return getLong(Arrays.copyOfRange(bytes, index, index + 8));
    }

    /**
     * Get a byte from a byte[].
     * @param bytes
     * @return
     */
    public static byte getByte(byte[] bytes) {
        return bytes[0];
    }

    /**
     * Get a String from a byte[].
     * @param bytes
     * @param start
     * @param end
     * @return
     */
    public static String getString(byte[] bytes, int start, int end) {
        byte[] stringBytes = Arrays.copyOfRange(bytes, start, end);
        return new String(stringBytes);
    }

    /**
     * Combines several byte[]s into one byte[].
     * @param arrays
     * @return
     */
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

    /**
     * Uses the version one of the protocol to convert objects into byte[].
     * @param name
     * @param objData
     * @return
     * @throws IOException
     */
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

    /**
     * This method handles turning a collection of objects into a byte[].
     * @param name
     * @param objData
     * @return
     * @throws IOException
     */
    private static byte[] handleCollection(String name, Collection objData) throws IOException {
        return handleObjectArray(name, objData.toArray());
    }

    /**
     * This method handles turning a map of objects into a byte[].
     * @param name
     * @param objData
     * @return
     * @throws IOException
     */
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

    /**
     * This method handles turning an array of objects into a byte[].
     * @param name
     * @param objData
     * @return
     * @throws IOException
     */
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
