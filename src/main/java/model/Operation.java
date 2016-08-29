package model;

/**
 * Created by nathaniel on 8/29/16.
 */
public enum Operation{

    GETTIME(0),
    CHANGETIME(1);

    public final int id;

    Operation(int id) {
        this.id = id;
    }

    public static Operation getOperation(int id){
        Operation[] ops = Operation.values();
        for(int i = 0; i < ops.length; ++i){
            if(ops[i].id == id)
                return ops[i];
        }
        throw new RuntimeException("No Such Operations");
    }
}
