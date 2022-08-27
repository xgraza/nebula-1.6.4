package net.minecraft.world.gen.layer;

public class GenLayerAddSnow extends GenLayer
{
    private static final String __OBFID = "CL_00000553";

    public GenLayerAddSnow(long par1, GenLayer par3GenLayer)
    {
        super(par1);
        this.parent = par3GenLayer;
    }

    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int var5 = par1 - 1;
        int var6 = par2 - 1;
        int var7 = par3 + 2;
        int var8 = par4 + 2;
        int[] var9 = this.parent.getInts(var5, var6, var7, var8);
        int[] var10 = IntCache.getIntCache(par3 * par4);

        for (int var11 = 0; var11 < par4; ++var11)
        {
            for (int var12 = 0; var12 < par3; ++var12)
            {
                int var13 = var9[var12 + 1 + (var11 + 1) * var7];
                this.initChunkSeed((long)(var12 + par1), (long)(var11 + par2));

                if (var13 == 0)
                {
                    var10[var12 + var11 * par3] = 0;
                }
                else
                {
                    int var14 = this.nextInt(6);
                    byte var15;

                    if (var14 == 0)
                    {
                        var15 = 4;
                    }
                    else if (var14 <= 1)
                    {
                        var15 = 3;
                    }
                    else
                    {
                        var15 = 1;
                    }

                    var10[var12 + var11 * par3] = var15;
                }
            }
        }

        return var10;
    }
}