package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class DerivedWorldInfo extends WorldInfo
{
    private final WorldInfo theWorldInfo;
    private static final String __OBFID = "CL_00000584";

    public DerivedWorldInfo(WorldInfo par1WorldInfo)
    {
        this.theWorldInfo = par1WorldInfo;
    }

    public NBTTagCompound getNBTTagCompound()
    {
        return this.theWorldInfo.getNBTTagCompound();
    }

    public NBTTagCompound cloneNBTCompound(NBTTagCompound par1NBTTagCompound)
    {
        return this.theWorldInfo.cloneNBTCompound(par1NBTTagCompound);
    }

    public long getSeed()
    {
        return this.theWorldInfo.getSeed();
    }

    public int getSpawnX()
    {
        return this.theWorldInfo.getSpawnX();
    }

    public int getSpawnY()
    {
        return this.theWorldInfo.getSpawnY();
    }

    public int getSpawnZ()
    {
        return this.theWorldInfo.getSpawnZ();
    }

    public long getWorldTotalTime()
    {
        return this.theWorldInfo.getWorldTotalTime();
    }

    public long getWorldTime()
    {
        return this.theWorldInfo.getWorldTime();
    }

    public long getSizeOnDisk()
    {
        return this.theWorldInfo.getSizeOnDisk();
    }

    public NBTTagCompound getPlayerNBTTagCompound()
    {
        return this.theWorldInfo.getPlayerNBTTagCompound();
    }

    public int getVanillaDimension()
    {
        return this.theWorldInfo.getVanillaDimension();
    }

    public String getWorldName()
    {
        return this.theWorldInfo.getWorldName();
    }

    public int getSaveVersion()
    {
        return this.theWorldInfo.getSaveVersion();
    }

    public long getLastTimePlayed()
    {
        return this.theWorldInfo.getLastTimePlayed();
    }

    public boolean isThundering()
    {
        return this.theWorldInfo.isThundering();
    }

    public int getThunderTime()
    {
        return this.theWorldInfo.getThunderTime();
    }

    public boolean isRaining()
    {
        return this.theWorldInfo.isRaining();
    }

    public int getRainTime()
    {
        return this.theWorldInfo.getRainTime();
    }

    public WorldSettings.GameType getGameType()
    {
        return this.theWorldInfo.getGameType();
    }

    public void setSpawnX(int par1) {}

    public void setSpawnY(int par1) {}

    public void setSpawnZ(int par1) {}

    public void incrementTotalWorldTime(long par1) {}

    public void setWorldTime(long par1) {}

    public void setSpawnPosition(int par1, int par2, int par3) {}

    public void setWorldName(String par1Str) {}

    public void setSaveVersion(int par1) {}

    public void setThundering(boolean par1) {}

    public void setThunderTime(int par1) {}

    public void setRaining(boolean par1) {}

    public void setRainTime(int par1) {}

    public boolean isMapFeaturesEnabled()
    {
        return this.theWorldInfo.isMapFeaturesEnabled();
    }

    public boolean isHardcoreModeEnabled()
    {
        return this.theWorldInfo.isHardcoreModeEnabled();
    }

    public WorldType getTerrainType()
    {
        return this.theWorldInfo.getTerrainType();
    }

    public void setTerrainType(WorldType par1WorldType) {}

    public boolean areCommandsAllowed()
    {
        return this.theWorldInfo.areCommandsAllowed();
    }

    public boolean isInitialized()
    {
        return this.theWorldInfo.isInitialized();
    }

    public void setServerInitialized(boolean par1) {}

    public GameRules getGameRulesInstance()
    {
        return this.theWorldInfo.getGameRulesInstance();
    }
}
