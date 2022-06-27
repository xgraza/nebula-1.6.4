package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableTileEntityID implements Callable
{
    final TileEntity theTileEntity;

    CallableTileEntityID(TileEntity par1TileEntity)
    {
        this.theTileEntity = par1TileEntity;
    }

    public String callTileEntityID()
    {
        int var1 = this.theTileEntity.worldObj.getBlockId(this.theTileEntity.xCoord, this.theTileEntity.yCoord, this.theTileEntity.zCoord);

        try
        {
            return String.format("ID #%d (%s // %s)", new Object[] {Integer.valueOf(var1), Block.blocksList[var1].getUnlocalizedName(), Block.blocksList[var1].getClass().getCanonicalName()});
        }
        catch (Throwable var3)
        {
            return "ID #" + var1;
        }
    }

    public Object call()
    {
        return this.callTileEntityID();
    }
}
