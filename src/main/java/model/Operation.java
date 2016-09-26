package model;

/**
 * {@link Operation} enum allows for an easy way of identifing the operations that are
 * associated with {@link Message}s.
 *
 * Created by nathaniel on 8/29/16.
 */
public enum Operation {

    GETTIME((short) 0),
    CHANGETIME((short) 1),
    GETTIMERETURN((short) 2),
    CHANGETIMERETURN((short)3);

    public final short id;

    Operation(short id) {
        this.id = id;
    }

    /**
     * Gets an operation based off of its identifier.
     *
     * @param id
     * @return
     */
    public static Operation getOperation(short id) {
        Operation[] ops = Operation.values();
        for (int i = 0; i < ops.length; ++i) {
            if (ops[i].id == id)
                return ops[i];
        }
        throw new RuntimeException("No Such Operations");
    }
}
