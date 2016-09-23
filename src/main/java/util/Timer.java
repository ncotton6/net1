package util;

/**
 * Created by nathaniel on 9/23/16.
 */
public class Timer {

    private long start, end;

    public void start() {
        start = System.currentTimeMillis();
    }

    public long end() {
        end = System.currentTimeMillis();
        return end - start;
    }

}
