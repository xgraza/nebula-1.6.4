package wtf.nebula.util;

public class Timer {
    private long time = -1L;

    public boolean passedTime(long delay, boolean reset) {
        // 1000000L
        boolean passed = System.nanoTime() - time >= delay * 1000000L;
        if (passed && reset) {
            resetTime();
        }

        return passed;
    }

    public long getTimePassedMs() {
        return (System.nanoTime() - time) / 1000000L;
    }

    public Timer resetTime() {
        time = System.nanoTime();
        return this;
    }
}
