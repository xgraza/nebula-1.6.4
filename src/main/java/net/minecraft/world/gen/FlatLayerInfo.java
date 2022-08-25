package net.minecraft.world.gen;

import net.minecraft.block.Block;

public class FlatLayerInfo
{
    private Block field_151537_a;
    private int layerCount;
    private int layerFillBlockMeta;
    private int layerMinimumY;
    private static final String __OBFID = "CL_00000441";

    public FlatLayerInfo(int p_i45467_1_, Block p_i45467_2_)
    {
        this.layerCount = 1;
        this.layerCount = p_i45467_1_;
        this.field_151537_a = p_i45467_2_;
    }

    public FlatLayerInfo(int p_i45468_1_, Block p_i45468_2_, int p_i45468_3_)
    {
        this(p_i45468_1_, p_i45468_2_);
        this.layerFillBlockMeta = p_i45468_3_;
    }

    public int getLayerCount()
    {
        return this.layerCount;
    }

    public Block func_151536_b()
    {
        return this.field_151537_a;
    }

    public int getFillBlockMeta()
    {
        return this.layerFillBlockMeta;
    }

    public int getMinY()
    {
        return this.layerMinimumY;
    }

    public void setMinY(int par1)
    {
        this.layerMinimumY = par1;
    }

    public String toString()
    {
        String var1 = Integer.toString(Block.getIdFromBlock(this.field_151537_a));

        if (this.layerCount > 1)
        {
            var1 = this.layerCount + "x" + var1;
        }

        if (this.layerFillBlockMeta > 0)
        {
            var1 = var1 + ":" + this.layerFillBlockMeta;
        }

        return var1;
    }
}
