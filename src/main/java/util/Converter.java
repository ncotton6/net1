package util;

import model.Convert;

import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * This {@link Converter} class holds all sorts of connections to {@link Convert} implementations to turn
 * a byte[] into usable objects.
 *
 * Created by nathaniel on 9/7/16.
 */
public class Converter {

    // Private Variables
    private static Converter converter;
    private Map<Byte, Map<Class<?>, Convert>> convertUnits = new HashMap<>();

    /**
     * The constructor for the converter is setup to be private, so that
     * it can behave as a singleton. It also sets up all sorts of conversion
     * handlers.
     */
    private Converter() {
        // Adds all sorts of conversion handlers.
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

    /**
     * Private get for convenience of internal methods to interact with the singleton.
     * @return Converter
     */
    private static Converter get() {
        if (converter == null)
            synchronized (Converter.class) {
                if (converter == null)
                    converter = new Converter();
            }
        return converter;
    }

    /**
     * This method will take in identifying information and produce an object
     * from the passed in byte[].
     * @param version
     * @param clazz
     * @param bytes
     * @return Object
     */
    public static Object convert(byte version, Class<?> clazz, byte[] bytes) {
        return get().getConverter(version,clazz).convert(bytes);
    }

    /**
     * This method will get the {@link Convert} implementor from the {@link Converter}
     * for the passed in information.
     * @param version
     * @param clazz
     * @return
     */
    public Convert getConverter(byte version, Class<?> clazz){
        if(convertUnits.containsKey(version))
            if(convertUnits.get(version).containsKey(clazz))
                return convertUnits.get(version).get(clazz);
        throw new RuntimeException("Cannot convert");
    }

}
