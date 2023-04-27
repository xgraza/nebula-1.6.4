package lol.nebula.util.math.timing;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class Timer {

    /**
     * A constant for converting milliseconds to nanoseconds
     * 1 million nanoseconds = 1 millisecond
     */
    private static final long ONE_MS_NS = 1_000_000L;

    /**
     * The time of one game tick in milliseconds
     */
    private static final long ONE_TICK_MS = 50L;

    /**
     * The start time for this timer
     */
    private long startTime;

    /**
     * Creates an instance of the timer
     */
    public Timer() {

        // reset time per instantiation of the object
        resetTime();
    }

    /**
     * Checks if a certain time has passed in game ticks
     * @param ticks the time in game ticks needed to pass
     * @param reset if to automatically reset the timer
     * @return if the time has passed
     */
    public boolean ticks(int ticks, boolean reset) {
        boolean passedTime = getTimeElapsedMS() >= (ticks * ONE_TICK_MS);
        if (passedTime && reset) resetTime();
        return passedTime;
    }

    /**
     * Checks if a certain time has passed in milliseconds
     * @param ms the time in milliseconds needed to pass
     * @param reset if to automatically reset the timer
     * @return if the time has passed
     */
    public boolean ms(long ms, boolean reset) {
        boolean passedTime = getTimeElapsedMS() >= ms;
        if (passedTime && reset) resetTime();
        return passedTime;
    }

    /**
     * Gets the start time
     * @return the start time in nanosecond
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Gets the time elapsed in nanoseconds
     * @return the elapsed time in nanoseconds
     */
    public long getTimeElapsed() {
        return System.nanoTime() - startTime;
    }

    /**
     * Gets the time elapsed in milliseconds
     * @return the elapsed time in milliseconds
     */
    public long getTimeElapsedMS() {
        return getTimeElapsed() / ONE_MS_NS;
    }

    /**
     * Resets the timer's time
     */
    public void resetTime() {
        startTime = System.nanoTime();
    }
}
