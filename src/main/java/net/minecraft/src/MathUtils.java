package net.minecraft.src;

import net.minecraft.util.MathHelper;

public class MathUtils
{
    public static int getAverage(int[] vals)
    {
        if (vals.length <= 0)
        {
            return 0;
        }
        else
        {
            int sum = getSum(vals);
            int avg = sum / vals.length;
            return avg;
        }
    }

    public static int getSum(int[] vals)
    {
        if (vals.length <= 0)
        {
            return 0;
        }
        else
        {
            int sum = 0;

            for (int i = 0; i < vals.length; ++i)
            {
                int val = vals[i];
                sum += val;
            }

            return sum;
        }
    }

    public static int roundDownToPowerOfTwo(int val)
    {
        int po2 = MathHelper.roundUpToPowerOfTwo(val);
        return val == po2 ? po2 : po2 / 2;
    }

    public static boolean equalsDelta(float f1, float f2, float delta)
    {
        return Math.abs(f1 - f2) <= delta;
    }

    public static float toDeg(float angle)
    {
        return angle * 180.0F / (float)Math.PI;
    }

    public static float toRad(float angle)
    {
        return angle / 180.0F * (float)Math.PI;
    }
}
