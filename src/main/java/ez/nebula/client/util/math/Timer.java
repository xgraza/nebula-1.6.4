package ez.nebula.client.util.math;

import java.util.concurrent.TimeUnit;

/**
 * @author xgraza
 * @since 02/19/25
 */
public final class Timer
{
    private long lastTimeNS;

    public Timer()
    {
        this(0L, TimeUnit.NANOSECONDS);
    }

    public Timer(final long offset, final TimeUnit unit)
    {
        lastTimeNS = System.nanoTime() + unit.convert(offset, TimeUnit.NANOSECONDS);
    }

    public boolean hasElapsed(final long ms)
    {
        return getTimeElapsedMS() >= ms;
    }

    public boolean hasElapsed(final int ticks)
    {
        final int elapsedTicks = (int) (getTimeElapsedMS() / 50);
        return elapsedTicks >= ticks;
    }

    public void resetTime()
    {
        lastTimeNS = System.nanoTime();
    }

    public double getTimeElapsedMS()
    {
        return getTimeElapsedNS() * 0.000001;
    }

    public long getTimeElapsedNS()
    {
        return System.nanoTime() - lastTimeNS;
    }
}
