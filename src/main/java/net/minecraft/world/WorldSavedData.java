package net.minecraft.world;

import net.minecraft.nbt.NBTTagCompound;

public abstract class WorldSavedData
{
    public final String mapName;
    private boolean dirty;
    private static final String __OBFID = "CL_00000580";

    public WorldSavedData(String par1Str)
    {
        this.mapName = par1Str;
    }

    public abstract void readFromNBT(NBTTagCompound var1);

    public abstract void writeToNBT(NBTTagCompound var1);

    public void markDirty()
    {
        this.setDirty(true);
    }

    public void setDirty(boolean par1)
    {
        this.dirty = par1;
    }

    public boolean isDirty()
    {
        return this.dirty;
    }
}
