package net.minecraft.world.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;

public class SaveHandlerMP implements ISaveHandler
{
    private static final String __OBFID = "CL_00000602";

    public WorldInfo loadWorldInfo()
    {
        return null;
    }

    public void checkSessionLock() throws MinecraftException {}

    public IChunkLoader getChunkLoader(WorldProvider par1WorldProvider)
    {
        return null;
    }

    public void saveWorldInfoWithPlayer(WorldInfo par1WorldInfo, NBTTagCompound par2NBTTagCompound) {}

    public void saveWorldInfo(WorldInfo par1WorldInfo) {}

    public IPlayerFileData getSaveHandler()
    {
        return null;
    }

    public void flush() {}

    public File getMapFileFromName(String par1Str)
    {
        return null;
    }

    public String getWorldDirectoryName()
    {
        return "none";
    }

    public File getWorldDirectory()
    {
        return null;
    }
}
