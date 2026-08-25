package ez.nebula.client.util.render.animation;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class Animation
{
    private final AnimationEasing easing;
    private double animationTimeMS;

    private boolean state;
    private long lastTimeMS;

    private double value, factor;

    public Animation(final AnimationEasing easing, final double animationTimeMS)
    {
        this.easing = easing;
        this.animationTimeMS = animationTimeMS;
        reset(true);
    }

    public double getValue()
    {
        return value;
    }

    public double getFactor()
    {
        return factor;
    }

    public double getEasedFactor()
    {
        final double diff = System.currentTimeMillis() - lastTimeMS;
        lastTimeMS = System.currentTimeMillis();
        value += ((diff / animationTimeMS) * (state ? 1 : -1));
        value = Math.min(Math.max(value, 0.0), 1.0);
        return (factor = easing.ease(value));
    }

    public void setState(boolean state)
    {
        this.state = state;
    }

    public void setAnimationTimeMS(double animationTimeMS)
    {
        this.animationTimeMS = animationTimeMS;
    }

    public void reset(boolean force)
    {
        if (!force && (factor > 0.0 && factor != 1.0))
        {
            return;
        }
        lastTimeMS = System.currentTimeMillis();
    }

    public boolean getState()
    {
        return state;
    }
}
