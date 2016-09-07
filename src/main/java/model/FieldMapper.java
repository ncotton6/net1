package model;

import util.ByteUtil;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by nathaniel on 8/29/16.
 */
public class FieldMapper extends HashMap<String, Integer> {

    private static FieldMapper fm = null;
    private static final String UNKNOWN = "unknown";

    private FieldMapper() {
        super();
        put(UNKNOWN, 0);
        put("user", 1);
        put("pass", 2);
        put("time", 3);
    }

    private static FieldMapper get() {
        if (fm == null)
            synchronized (FieldMapper.class) {
                if (fm == null)
                    fm = new FieldMapper();
            }
        return fm;
    }

    public static byte[] lookupName(String name) {
        FieldMapper fm = get();
        if (fm.containsKey(name)) {
            return ByteUtil.getBytes(fm.get(name));
        } else {
            byte[] id = ByteUtil.getBytes(fm.get(UNKNOWN));
            byte[] nameInBytes = name.getBytes();
            byte[] length = ByteUtil.getBytes(nameInBytes.length);
            return ByteUtil.combine(id,length,nameInBytes);
        }
    }

    public static String reverseLookup(int identifier) {
        FieldMapper fm = get();
        System.out.println(identifier);
        Optional<Entry<String, Integer>> value = fm.entrySet().stream().filter((ent)->ent.getValue()==identifier).findFirst();
        if(value.isPresent())
            return value.get().getKey();
        throw new RuntimeException("Failed to find reverse lookup");
    }
}
