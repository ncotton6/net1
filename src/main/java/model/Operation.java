package model;

/**
 * Created by nathaniel on 8/29/16.
 */
public enum Operation{

    GETTIME(0),
    CHANGETIME(1);

    private final int id;

    Operation(int id) {
        this.id = id;
    }
}
