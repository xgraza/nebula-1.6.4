package net.minecraft.src;

import java.util.concurrent.Callable;

final class CallableBlockType implements Callable
{
    final int blockID;

    CallableBlockType(int par1)
    {
        this.blockID = par1;
    }

    public String callBlockType()
    {
        try
        {
            return String.format("ID #%d (%s // %s)", new Object[] {Integer.valueOf(this.blockID), Block.blocksList[this.blockID].getUnlocalizedName(), Block.blocksList[this.blockID].getClass().getCanonicalName()});
        }
        catch (Throwable var2)
        {
            return "ID #" + this.blockID;
        }
    }

    public Object call()
    {
        return this.callBlockType();
    }
}
