package net.minecraft.util;

import java.util.ArrayList;
import java.util.List;

public class AABBPool
{
    private final int maxNumCleans;
    private final int numEntriesToRemove;
    private final List listAABB = new ArrayList();
    private int nextPoolIndex;
    private int maxPoolIndex;
    private int numCleans;
    private static final String __OBFID = "CL_00000609";

    public AABBPool(int par1, int par2)
    {
        this.maxNumCleans = par1;
        this.numEntriesToRemove = par2;
    }

    public AxisAlignedBB getAABB(double par1, double par3, double par5, double par7, double par9, double par11)
    {
        AxisAlignedBB var13;

        if (this.nextPoolIndex >= this.listAABB.size())
        {
            var13 = new AxisAlignedBB(par1, par3, par5, par7, par9, par11);
            this.listAABB.add(var13);
        }
        else
        {
            var13 = (AxisAlignedBB)this.listAABB.get(this.nextPoolIndex);
            var13.setBounds(par1, par3, par5, par7, par9, par11);
        }

        ++this.nextPoolIndex;
        return var13;
    }

    public void cleanPool()
    {
        if (this.nextPoolIndex > this.maxPoolIndex)
        {
            this.maxPoolIndex = this.nextPoolIndex;
        }

        if (this.numCleans++ == this.maxNumCleans)
        {
            int var1 = Math.max(this.maxPoolIndex, this.listAABB.size() - this.numEntriesToRemove);

            while (this.listAABB.size() > var1)
            {
                this.listAABB.remove(var1);
            }

            this.maxPoolIndex = 0;
            this.numCleans = 0;
        }

        this.nextPoolIndex = 0;
    }

    public void clearPool()
    {
        this.nextPoolIndex = 0;
        this.listAABB.clear();
    }

    public int getlistAABBsize()
    {
        return this.listAABB.size();
    }

    public int getnextPoolIndex()
    {
        return this.nextPoolIndex;
    }
}
