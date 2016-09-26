package util;

/**
 * Utility class to keep track of how long things take.
 *
 * Created by nathaniel on 9/23/16.
 */
public class Timer {

    private long start, end;

    /**
     * Starts the timer.
     * @return Timer
     */
    public static Timer start(){
        Timer t = new Timer();
        t.start = System.currentTimeMillis();
        return t;
    }

    /**
     * Stops the timer and produces the elapsed time.
     * @return long
     */
    public long end() {
        end = System.currentTimeMillis();
        return end - start;
    }

}
