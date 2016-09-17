package util;

import model.Convert;

import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * Created by nathaniel on 9/7/16.
 */
public class Converter {

    private static Converter converter;
    private Map<Byte, Map<Class<?>, Convert>> convertUnits = new HashMap<>();

    private Converter() {
        Map<Class<?>, Convert> version1 = new HashMap<>();
        version1.put(long.class,ByteUtil::getLong);
        version1.put(int.class,ByteUtil::getInt);
        version1.put(short.class,ByteUtil::getShort);
        version1.put(byte.class,ByteUtil::getByte);
        version1.put(Integer.class, ByteUtil::getInt);
        version1.put(Byte.class, ByteUtil::getByte);
        version1.put(Short.class, ByteUtil::getShort);
        version1.put(String.class, String::new);
        version1.put(String[].class, (bytes -> {
            List<String> strs = new ArrayList<>();
            int index = 0;
            while (index < bytes.length) {
                int length = ByteUtil.getInt(bytes, index);
                index += 4;
                String s = ByteUtil.getString(bytes, index, index + length);
                index += length;
                strs.add(s);
            }
            return strs.toArray(new String[strs.size()]);
        }));
        version1.put(int[].class, (bytes) -> {
            int[] ints = new int[bytes.length / 4];
            int index = 0;
            int index2 = 0;
            while (index < bytes.length) {
                ints[index2] = ByteUtil.getInt(bytes, index);
                index += 4;
                ++index2;
            }
            return ints;
        });
        version1.put(long[].class,(bytes -> {
            long[] longs = new long[bytes.length / 8];
            int index = 0;
            int index2 = 0;
            while(index < bytes.length){
                longs[index2] = ByteUtil.getLong(bytes,index);
                index += 8;
                ++index2;
            }
            return longs;
        }));
        convertUnits.put((byte) 1, version1);
    }

    private static Converter get() {
        if (converter == null)
            synchronized (Converter.class) {
                if (converter == null)
                    converter = new Converter();
            }
        return converter;
    }

    public static Object convert(byte version, Class<?> clazz, byte[] bytes) {
        return get().getConverter(version,clazz).convert(bytes);
    }


    public Convert getConverter(byte version, Class<?> clazz){
        if(convertUnits.containsKey(version))
            if(convertUnits.get(version).containsKey(clazz))
                return convertUnits.get(version).get(clazz);
        throw new RuntimeException("Cannot convert");
    }

}
