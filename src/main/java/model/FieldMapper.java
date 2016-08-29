package model;

import java.util.HashMap;

/**
 * Created by nathaniel on 8/29/16.
 */
public class FieldMapper extends HashMap<String, Integer> {

    private static FieldMapper fm = null;

    private FieldMapper() {
        super();
        put("user",1);
        put("pass",2);
        put("time",3);
    }

    public static int getId(String name) {
        if (fm == null)
            synchronized (FieldMapper.class) {
                if (fm == null)
                    fm = new FieldMapper();
            }
        if (fm.containsKey(name))
            return fm.get(name);
        throw new RuntimeException("Failed to Map the Field");
    }
}
