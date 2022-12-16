package wtf.nebula.client.utils.client;

public class Timer {
    private long time = -1L;

    public boolean hasPassed(long delay, boolean reset) {
        boolean result = System.nanoTime() - time >= delay * 1000000L;
        if (reset && result) {
            resetTime();
        }

        return result;
    }

    public void resetTime() {
        time = System.nanoTime();
    }

    public long getTimePassedMs() {
        return (System.nanoTime() - time) / 1000000L;
    }
}
