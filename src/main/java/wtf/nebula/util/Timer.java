package wtf.nebula.util;

public class Timer {
    private long time = -1L;

    public boolean passedTime(long delay, boolean reset) {
        // 1000000L
        boolean passed = System.nanoTime() - time >= delay / 1000000L;
        if (passed && reset) {
            resetTime();
        }

        return passed;
    }

    public Timer resetTime() {
        time = System.nanoTime();
        return this;
    }
}
