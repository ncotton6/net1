package model;

/**
 * Created by nathaniel on 8/29/16.
 */
public enum Operation {

    GETTIME((short) 0),
    CHANGETIME((short) 1);

    public final short id;

    Operation(short id) {
        this.id = id;
    }

    public static Operation getOperation(short id) {
        Operation[] ops = Operation.values();
        for (int i = 0; i < ops.length; ++i) {
            if (ops[i].id == id)
                return ops[i];
        }
        throw new RuntimeException("No Such Operations");
    }
}
