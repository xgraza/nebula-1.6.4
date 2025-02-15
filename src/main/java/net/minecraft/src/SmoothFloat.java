package net.minecraft.src;

public class SmoothFloat
{
    private float valueLast;
    private final float timeFadeUpSec;
    private final float timeFadeDownSec;
    private long timeLastMs;

    public SmoothFloat(float valueLast, float timeFadeSec)
    {
        this(valueLast, timeFadeSec, timeFadeSec);
    }

    public SmoothFloat(float valueLast, float timeFadeUpSec, float timeFadeDownSec)
    {
        this.valueLast = valueLast;
        this.timeFadeUpSec = timeFadeUpSec;
        this.timeFadeDownSec = timeFadeDownSec;
        this.timeLastMs = System.currentTimeMillis();
    }

    public float getValueLast()
    {
        return this.valueLast;
    }

    public float getTimeFadeUpSec()
    {
        return this.timeFadeUpSec;
    }

    public float getTimeFadeDownSec()
    {
        return this.timeFadeDownSec;
    }

    public long getTimeLastMs()
    {
        return this.timeLastMs;
    }

    public float getSmoothValue(float value)
    {
        long timeNowMs = System.currentTimeMillis();
        float valPrev = this.valueLast;
        long timePrevMs = this.timeLastMs;
        float timeDeltaSec = (float)(timeNowMs - timePrevMs) / 1000.0F;
        float timeFadeSec = value >= valPrev ? this.timeFadeUpSec : this.timeFadeDownSec;
        float valSmooth = getSmoothValue(valPrev, value, timeDeltaSec, timeFadeSec);
        this.valueLast = valSmooth;
        this.timeLastMs = timeNowMs;
        return valSmooth;
    }

    public static float getSmoothValue(float valPrev, float value, float timeDeltaSec, float timeFadeSec)
    {
        if (timeDeltaSec <= 0.0F)
        {
            return valPrev;
        }
        else
        {
            float valDelta = value - valPrev;
            float valSmooth;

            if (timeFadeSec > 0.0F && timeDeltaSec < timeFadeSec && Math.abs(valDelta) > 1.0E-6F)
            {
                float countUpdates = timeFadeSec / timeDeltaSec;
                float k1 = 4.61F;
                float k2 = 0.13F;
                float k3 = 10.0F;
                float kCorr = k1 - 1.0F / (k2 + countUpdates / k3);
                float kTime = timeDeltaSec / timeFadeSec * kCorr;
                kTime = NumUtils.limit(kTime, 0.0F, 1.0F);
                valSmooth = valPrev + valDelta * kTime;
            }
            else
            {
                valSmooth = value;
            }

            return valSmooth;
        }
    }
}
