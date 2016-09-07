package util;

import model.ByteArray;
import model.Field;
import model.FieldMapper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by nathaniel on 8/29/16.
 */
public class ByteUtil {

    public static byte[] getBytes(int num) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(num);
        return bb.array();
    }

    public static int getInt(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb.getInt();
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
        if (objData instanceof byte[])
            return (byte[]) objData;
        else if (objData instanceof String)
            return ((String) objData).getBytes();
        else if (objData instanceof Integer)
            return ByteUtil.getBytes((int) objData);
        else if (objData instanceof Field)
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
        for(Object key : keys){
            if(key instanceof String){
                baos.write(convert((String)key,objData.get(key)));
            }else{
                throw new RuntimeException("Maps being converted to bytes need to have strings for keys.");
            }
        }
        byte[] bytes = baos.toByteArray();
        byte[] length = getBytes(bytes.length);
        baos.close();
        if (name != null) {
            byte[] identifier = FieldMapper.lookupName(name);
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
            byte[] identifier = FieldMapper.lookupName(name);
            return ByteUtil.combine(identifier, length, bytes);
        } else {
            return ByteUtil.combine(length, bytes);
        }
    }

}
