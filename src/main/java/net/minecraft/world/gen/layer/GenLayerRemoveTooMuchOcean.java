package net.minecraft.world.gen.layer;

public class GenLayerRemoveTooMuchOcean extends GenLayer
{
    private static final String __OBFID = "CL_00000564";

    public GenLayerRemoveTooMuchOcean(long p_i45480_1_, GenLayer p_i45480_3_)
    {
        super(p_i45480_1_);
        this.parent = p_i45480_3_;
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
                int var13 = var9[var12 + 1 + (var11 + 1 - 1) * (par3 + 2)];
                int var14 = var9[var12 + 1 + 1 + (var11 + 1) * (par3 + 2)];
                int var15 = var9[var12 + 1 - 1 + (var11 + 1) * (par3 + 2)];
                int var16 = var9[var12 + 1 + (var11 + 1 + 1) * (par3 + 2)];
                int var17 = var9[var12 + 1 + (var11 + 1) * var7];
                var10[var12 + var11 * par3] = var17;
                this.initChunkSeed((long)(var12 + par1), (long)(var11 + par2));

                if (var17 == 0 && var13 == 0 && var14 == 0 && var15 == 0 && var16 == 0 && this.nextInt(2) == 0)
                {
                    var10[var12 + var11 * par3] = 1;
                }
            }
        }

        return var10;
    }
}