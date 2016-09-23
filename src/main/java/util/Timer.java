package util;

/**
 * Created by nathaniel on 9/23/16.
 */
public class Timer {

    private long start, end;

    public static Timer start(){
        Timer t = new Timer();
        t.start = System.currentTimeMillis();
        return t;
    }

    public long end() {
        end = System.currentTimeMillis();
        return end - start;
    }

}
