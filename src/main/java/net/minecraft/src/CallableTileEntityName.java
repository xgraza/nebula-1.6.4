package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableTileEntityName implements Callable
{
    final TileEntity theTileEntity;

    CallableTileEntityName(TileEntity par1TileEntity)
    {
        this.theTileEntity = par1TileEntity;
    }

    public String callTileEntityName()
    {
        return (String)TileEntity.getClassToNameMap().get(this.theTileEntity.getClass()) + " // " + this.theTileEntity.getClass().getCanonicalName();
    }

    public Object call()
    {
        return this.callTileEntityName();
    }
}
