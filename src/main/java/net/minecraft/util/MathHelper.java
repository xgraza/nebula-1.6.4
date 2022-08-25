package net.minecraft.util;

import java.util.Random;

public class MathHelper
{
    private static final int SIN_BITS = 12;
    private static final int SIN_MASK = 4095;
    private static final int SIN_COUNT = 4096;
    public static final float PI = (float)Math.PI;
    public static final float PI2 = ((float)Math.PI * 2F);
    public static final float PId2 = ((float)Math.PI / 2F);
    private static final float radFull = ((float)Math.PI * 2F);
    private static final float degFull = 360.0F;
    private static final float radToIndex = 651.8986F;
    private static final float degToIndex = 11.377778F;
    public static final float deg2Rad = 0.017453292F;
    private static final float[] SIN_TABLE_FAST = new float[4096];
    public static boolean fastMath = false;
    private static float[] SIN_TABLE = new float[65536];
    private static final int[] multiplyDeBruijnBitPosition;

    public static final float sin(float par0)
    {
        return fastMath ? SIN_TABLE_FAST[(int)(par0 * 651.8986F) & 4095] : SIN_TABLE[(int)(par0 * 10430.378F) & 65535];
    }

    public static final float cos(float par0)
    {
        return fastMath ? SIN_TABLE_FAST[(int)((par0 + ((float)Math.PI / 2F)) * 651.8986F) & 4095] : SIN_TABLE[(int)(par0 * 10430.378F + 16384.0F) & 65535];
    }

    public static final float sqrt_float(float par0)
    {
        return (float)Math.sqrt((double)par0);
    }

    public static final float sqrt_double(double par0)
    {
        return (float)Math.sqrt(par0);
    }

    public static int floor_float(float par0)
    {
        int var1 = (int)par0;
        return par0 < (float)var1 ? var1 - 1 : var1;
    }

    public static int truncateDoubleToInt(double par0)
    {
        return (int)(par0 + 1024.0D) - 1024;
    }

    public static int floor_double(double par0)
    {
        int var2 = (int)par0;
        return par0 < (double)var2 ? var2 - 1 : var2;
    }

    public static long floor_double_long(double par0)
    {
        long var2 = (long)par0;
        return par0 < (double)var2 ? var2 - 1L : var2;
    }

    public static float abs(float par0)
    {
        return par0 >= 0.0F ? par0 : -par0;
    }

    public static int abs_int(int par0)
    {
        return par0 >= 0 ? par0 : -par0;
    }

    public static int ceiling_float_int(float par0)
    {
        int var1 = (int)par0;
        return par0 > (float)var1 ? var1 + 1 : var1;
    }

    public static int ceiling_double_int(double par0)
    {
        int var2 = (int)par0;
        return par0 > (double)var2 ? var2 + 1 : var2;
    }

    public static int clamp_int(int par0, int par1, int par2)
    {
        return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
    }

    public static float clamp_float(float par0, float par1, float par2)
    {
        return par0 < par1 ? par1 : (par0 > par2 ? par2 : par0);
    }

    public static double clamp_double(double p_151237_0_, double p_151237_2_, double p_151237_4_)
    {
        return p_151237_0_ < p_151237_2_ ? p_151237_2_ : (p_151237_0_ > p_151237_4_ ? p_151237_4_ : p_151237_0_);
    }

    public static double denormalizeClamp(double p_151238_0_, double p_151238_2_, double p_151238_4_)
    {
        return p_151238_4_ < 0.0D ? p_151238_0_ : (p_151238_4_ > 1.0D ? p_151238_2_ : p_151238_0_ + (p_151238_2_ - p_151238_0_) * p_151238_4_);
    }

    public static double abs_max(double par0, double par2)
    {
        if (par0 < 0.0D)
        {
            par0 = -par0;
        }

        if (par2 < 0.0D)
        {
            par2 = -par2;
        }

        return par0 > par2 ? par0 : par2;
    }

    public static int bucketInt(int par0, int par1)
    {
        return par0 < 0 ? -((-par0 - 1) / par1) - 1 : par0 / par1;
    }

    public static boolean stringNullOrLengthZero(String par0Str)
    {
        return par0Str == null || par0Str.length() == 0;
    }

    public static int getRandomIntegerInRange(Random par0Random, int par1, int par2)
    {
        return par1 >= par2 ? par1 : par0Random.nextInt(par2 - par1 + 1) + par1;
    }

    public static float randomFloatClamp(Random p_151240_0_, float p_151240_1_, float p_151240_2_)
    {
        return p_151240_1_ >= p_151240_2_ ? p_151240_1_ : p_151240_0_.nextFloat() * (p_151240_2_ - p_151240_1_) + p_151240_1_;
    }

    public static double getRandomDoubleInRange(Random par0Random, double par1, double par3)
    {
        return par1 >= par3 ? par1 : par0Random.nextDouble() * (par3 - par1) + par1;
    }

    public static double average(long[] par0ArrayOfLong)
    {
        long var1 = 0L;
        long[] var3 = par0ArrayOfLong;
        int var4 = par0ArrayOfLong.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            long var6 = var3[var5];
            var1 += var6;
        }

        return (double)var1 / (double)par0ArrayOfLong.length;
    }

    public static float wrapAngleTo180_float(float par0)
    {
        par0 %= 360.0F;

        if (par0 >= 180.0F)
        {
            par0 -= 360.0F;
        }

        if (par0 < -180.0F)
        {
            par0 += 360.0F;
        }

        return par0;
    }

    public static double wrapAngleTo180_double(double par0)
    {
        par0 %= 360.0D;

        if (par0 >= 180.0D)
        {
            par0 -= 360.0D;
        }

        if (par0 < -180.0D)
        {
            par0 += 360.0D;
        }

        return par0;
    }

    public static int parseIntWithDefault(String par0Str, int par1)
    {
        int var2 = par1;

        try
        {
            var2 = Integer.parseInt(par0Str);
        }
        catch (Throwable var4)
        {
            ;
        }

        return var2;
    }

    public static int parseIntWithDefaultAndMax(String par0Str, int par1, int par2)
    {
        int var3 = par1;

        try
        {
            var3 = Integer.parseInt(par0Str);
        }
        catch (Throwable var5)
        {
            ;
        }

        if (var3 < par2)
        {
            var3 = par2;
        }

        return var3;
    }

    public static double parseDoubleWithDefault(String par0Str, double par1)
    {
        double var3 = par1;

        try
        {
            var3 = Double.parseDouble(par0Str);
        }
        catch (Throwable var6)
        {
            ;
        }

        return var3;
    }

    public static double parseDoubleWithDefaultAndMax(String par0Str, double par1, double par3)
    {
        double var5 = par1;

        try
        {
            var5 = Double.parseDouble(par0Str);
        }
        catch (Throwable var8)
        {
            ;
        }

        if (var5 < par3)
        {
            var5 = par3;
        }

        return var5;
    }

    public static int roundUpToPowerOfTwo(int p_151236_0_)
    {
        int var1 = p_151236_0_ - 1;
        var1 |= var1 >> 1;
        var1 |= var1 >> 2;
        var1 |= var1 >> 4;
        var1 |= var1 >> 8;
        var1 |= var1 >> 16;
        return var1 + 1;
    }

    private static boolean isPowerOfTwo(int p_151235_0_)
    {
        return p_151235_0_ != 0 && (p_151235_0_ & p_151235_0_ - 1) == 0;
    }

    private static int calculateLogBaseTwoDeBruijn(int p_151241_0_)
    {
        p_151241_0_ = isPowerOfTwo(p_151241_0_) ? p_151241_0_ : roundUpToPowerOfTwo(p_151241_0_);
        return multiplyDeBruijnBitPosition[(int)((long)p_151241_0_ * 125613361L >> 27) & 31];
    }

    public static int calculateLogBaseTwo(int p_151239_0_)
    {
        return calculateLogBaseTwoDeBruijn(p_151239_0_) - (isPowerOfTwo(p_151239_0_) ? 0 : 1);
    }

    static
    {
        int i;

        for (i = 0; i < 65536; ++i)
        {
            SIN_TABLE[i] = (float)Math.sin((double)i * Math.PI * 2.0D / 65536.0D);
        }

        multiplyDeBruijnBitPosition = new int[] {0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};

        for (i = 0; i < 4096; ++i)
        {
            SIN_TABLE_FAST[i] = (float)Math.sin((double)(((float)i + 0.5F) / 4096.0F * ((float)Math.PI * 2F)));
        }

        for (i = 0; i < 360; i += 90)
        {
            SIN_TABLE_FAST[(int)((float)i * 11.377778F) & 4095] = (float)Math.sin((double)((float)i * 0.017453292F));
        }
    }
}
