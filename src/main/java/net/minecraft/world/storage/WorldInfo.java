package net.minecraft.world.storage;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class WorldInfo
{
    private long randomSeed;
    private WorldType terrainType;
    private String generatorOptions;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private long totalTime;
    private long worldTime;
    private long lastTimePlayed;
    private long sizeOnDisk;
    private NBTTagCompound playerTag;
    private int dimension;
    private String levelName;
    private int saveVersion;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private WorldSettings.GameType theGameType;
    private boolean mapFeaturesEnabled;
    private boolean hardcore;
    private boolean allowCommands;
    private boolean initialized;
    private GameRules theGameRules;
    private static final String __OBFID = "CL_00000587";

    protected WorldInfo()
    {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
    }

    public WorldInfo(NBTTagCompound par1NBTTagCompound)
    {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
        this.randomSeed = par1NBTTagCompound.getLong("RandomSeed");

        if (par1NBTTagCompound.hasKey("generatorName", 8))
        {
            String var2 = par1NBTTagCompound.getString("generatorName");
            this.terrainType = WorldType.parseWorldType(var2);

            if (this.terrainType == null)
            {
                this.terrainType = WorldType.DEFAULT;
            }
            else if (this.terrainType.isVersioned())
            {
                int var3 = 0;

                if (par1NBTTagCompound.hasKey("generatorVersion", 99))
                {
                    var3 = par1NBTTagCompound.getInteger("generatorVersion");
                }

                this.terrainType = this.terrainType.getWorldTypeForGeneratorVersion(var3);
            }

            if (par1NBTTagCompound.hasKey("generatorOptions", 8))
            {
                this.generatorOptions = par1NBTTagCompound.getString("generatorOptions");
            }
        }

        this.theGameType = WorldSettings.GameType.getByID(par1NBTTagCompound.getInteger("GameType"));

        if (par1NBTTagCompound.hasKey("MapFeatures", 99))
        {
            this.mapFeaturesEnabled = par1NBTTagCompound.getBoolean("MapFeatures");
        }
        else
        {
            this.mapFeaturesEnabled = true;
        }

        this.spawnX = par1NBTTagCompound.getInteger("SpawnX");
        this.spawnY = par1NBTTagCompound.getInteger("SpawnY");
        this.spawnZ = par1NBTTagCompound.getInteger("SpawnZ");
        this.totalTime = par1NBTTagCompound.getLong("Time");

        if (par1NBTTagCompound.hasKey("DayTime", 99))
        {
            this.worldTime = par1NBTTagCompound.getLong("DayTime");
        }
        else
        {
            this.worldTime = this.totalTime;
        }

        this.lastTimePlayed = par1NBTTagCompound.getLong("LastPlayed");
        this.sizeOnDisk = par1NBTTagCompound.getLong("SizeOnDisk");
        this.levelName = par1NBTTagCompound.getString("LevelName");
        this.saveVersion = par1NBTTagCompound.getInteger("version");
        this.rainTime = par1NBTTagCompound.getInteger("rainTime");
        this.raining = par1NBTTagCompound.getBoolean("raining");
        this.thunderTime = par1NBTTagCompound.getInteger("thunderTime");
        this.thundering = par1NBTTagCompound.getBoolean("thundering");
        this.hardcore = par1NBTTagCompound.getBoolean("hardcore");

        if (par1NBTTagCompound.hasKey("initialized", 99))
        {
            this.initialized = par1NBTTagCompound.getBoolean("initialized");
        }
        else
        {
            this.initialized = true;
        }

        if (par1NBTTagCompound.hasKey("allowCommands", 99))
        {
            this.allowCommands = par1NBTTagCompound.getBoolean("allowCommands");
        }
        else
        {
            this.allowCommands = this.theGameType == WorldSettings.GameType.CREATIVE;
        }

        if (par1NBTTagCompound.hasKey("Player", 10))
        {
            this.playerTag = par1NBTTagCompound.getCompoundTag("Player");
            this.dimension = this.playerTag.getInteger("Dimension");
        }

        if (par1NBTTagCompound.hasKey("GameRules", 10))
        {
            this.theGameRules.readGameRulesFromNBT(par1NBTTagCompound.getCompoundTag("GameRules"));
        }
    }

    public WorldInfo(WorldSettings par1WorldSettings, String par2Str)
    {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
        this.randomSeed = par1WorldSettings.getSeed();
        this.theGameType = par1WorldSettings.getGameType();
        this.mapFeaturesEnabled = par1WorldSettings.isMapFeaturesEnabled();
        this.levelName = par2Str;
        this.hardcore = par1WorldSettings.getHardcoreEnabled();
        this.terrainType = par1WorldSettings.getTerrainType();
        this.generatorOptions = par1WorldSettings.func_82749_j();
        this.allowCommands = par1WorldSettings.areCommandsAllowed();
        this.initialized = false;
    }

    public WorldInfo(WorldInfo par1WorldInfo)
    {
        this.terrainType = WorldType.DEFAULT;
        this.generatorOptions = "";
        this.theGameRules = new GameRules();
        this.randomSeed = par1WorldInfo.randomSeed;
        this.terrainType = par1WorldInfo.terrainType;
        this.generatorOptions = par1WorldInfo.generatorOptions;
        this.theGameType = par1WorldInfo.theGameType;
        this.mapFeaturesEnabled = par1WorldInfo.mapFeaturesEnabled;
        this.spawnX = par1WorldInfo.spawnX;
        this.spawnY = par1WorldInfo.spawnY;
        this.spawnZ = par1WorldInfo.spawnZ;
        this.totalTime = par1WorldInfo.totalTime;
        this.worldTime = par1WorldInfo.worldTime;
        this.lastTimePlayed = par1WorldInfo.lastTimePlayed;
        this.sizeOnDisk = par1WorldInfo.sizeOnDisk;
        this.playerTag = par1WorldInfo.playerTag;
        this.dimension = par1WorldInfo.dimension;
        this.levelName = par1WorldInfo.levelName;
        this.saveVersion = par1WorldInfo.saveVersion;
        this.rainTime = par1WorldInfo.rainTime;
        this.raining = par1WorldInfo.raining;
        this.thunderTime = par1WorldInfo.thunderTime;
        this.thundering = par1WorldInfo.thundering;
        this.hardcore = par1WorldInfo.hardcore;
        this.allowCommands = par1WorldInfo.allowCommands;
        this.initialized = par1WorldInfo.initialized;
        this.theGameRules = par1WorldInfo.theGameRules;
    }

    public NBTTagCompound getNBTTagCompound()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        this.updateTagCompound(var1, this.playerTag);
        return var1;
    }

    public NBTTagCompound cloneNBTCompound(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagCompound var2 = new NBTTagCompound();
        this.updateTagCompound(var2, par1NBTTagCompound);
        return var2;
    }

    private void updateTagCompound(NBTTagCompound par1NBTTagCompound, NBTTagCompound par2NBTTagCompound)
    {
        par1NBTTagCompound.setLong("RandomSeed", this.randomSeed);
        par1NBTTagCompound.setString("generatorName", this.terrainType.getWorldTypeName());
        par1NBTTagCompound.setInteger("generatorVersion", this.terrainType.getGeneratorVersion());
        par1NBTTagCompound.setString("generatorOptions", this.generatorOptions);
        par1NBTTagCompound.setInteger("GameType", this.theGameType.getID());
        par1NBTTagCompound.setBoolean("MapFeatures", this.mapFeaturesEnabled);
        par1NBTTagCompound.setInteger("SpawnX", this.spawnX);
        par1NBTTagCompound.setInteger("SpawnY", this.spawnY);
        par1NBTTagCompound.setInteger("SpawnZ", this.spawnZ);
        par1NBTTagCompound.setLong("Time", this.totalTime);
        par1NBTTagCompound.setLong("DayTime", this.worldTime);
        par1NBTTagCompound.setLong("SizeOnDisk", this.sizeOnDisk);
        par1NBTTagCompound.setLong("LastPlayed", MinecraftServer.getSystemTimeMillis());
        par1NBTTagCompound.setString("LevelName", this.levelName);
        par1NBTTagCompound.setInteger("version", this.saveVersion);
        par1NBTTagCompound.setInteger("rainTime", this.rainTime);
        par1NBTTagCompound.setBoolean("raining", this.raining);
        par1NBTTagCompound.setInteger("thunderTime", this.thunderTime);
        par1NBTTagCompound.setBoolean("thundering", this.thundering);
        par1NBTTagCompound.setBoolean("hardcore", this.hardcore);
        par1NBTTagCompound.setBoolean("allowCommands", this.allowCommands);
        par1NBTTagCompound.setBoolean("initialized", this.initialized);
        par1NBTTagCompound.setTag("GameRules", this.theGameRules.writeGameRulesToNBT());

        if (par2NBTTagCompound != null)
        {
            par1NBTTagCompound.setTag("Player", par2NBTTagCompound);
        }
    }

    public long getSeed()
    {
        return this.randomSeed;
    }

    public int getSpawnX()
    {
        return this.spawnX;
    }

    public int getSpawnY()
    {
        return this.spawnY;
    }

    public int getSpawnZ()
    {
        return this.spawnZ;
    }

    public long getWorldTotalTime()
    {
        return this.totalTime;
    }

    public long getWorldTime()
    {
        return this.worldTime;
    }

    public long getSizeOnDisk()
    {
        return this.sizeOnDisk;
    }

    public NBTTagCompound getPlayerNBTTagCompound()
    {
        return this.playerTag;
    }

    public int getVanillaDimension()
    {
        return this.dimension;
    }

    public void setSpawnX(int par1)
    {
        this.spawnX = par1;
    }

    public void setSpawnY(int par1)
    {
        this.spawnY = par1;
    }

    public void setSpawnZ(int par1)
    {
        this.spawnZ = par1;
    }

    public void incrementTotalWorldTime(long par1)
    {
        this.totalTime = par1;
    }

    public void setWorldTime(long par1)
    {
        this.worldTime = par1;
    }

    public void setSpawnPosition(int par1, int par2, int par3)
    {
        this.spawnX = par1;
        this.spawnY = par2;
        this.spawnZ = par3;
    }

    public String getWorldName()
    {
        return this.levelName;
    }

    public void setWorldName(String par1Str)
    {
        this.levelName = par1Str;
    }

    public int getSaveVersion()
    {
        return this.saveVersion;
    }

    public void setSaveVersion(int par1)
    {
        this.saveVersion = par1;
    }

    public long getLastTimePlayed()
    {
        return this.lastTimePlayed;
    }

    public boolean isThundering()
    {
        return this.thundering;
    }

    public void setThundering(boolean par1)
    {
        this.thundering = par1;
    }

    public int getThunderTime()
    {
        return this.thunderTime;
    }

    public void setThunderTime(int par1)
    {
        this.thunderTime = par1;
    }

    public boolean isRaining()
    {
        return this.raining;
    }

    public void setRaining(boolean par1)
    {
        this.raining = par1;
    }

    public int getRainTime()
    {
        return this.rainTime;
    }

    public void setRainTime(int par1)
    {
        this.rainTime = par1;
    }

    public WorldSettings.GameType getGameType()
    {
        return this.theGameType;
    }

    public boolean isMapFeaturesEnabled()
    {
        return this.mapFeaturesEnabled;
    }

    public void setGameType(WorldSettings.GameType par1EnumGameType)
    {
        this.theGameType = par1EnumGameType;
    }

    public boolean isHardcoreModeEnabled()
    {
        return this.hardcore;
    }

    public WorldType getTerrainType()
    {
        return this.terrainType;
    }

    public void setTerrainType(WorldType par1WorldType)
    {
        this.terrainType = par1WorldType;
    }

    public String getGeneratorOptions()
    {
        return this.generatorOptions;
    }

    public boolean areCommandsAllowed()
    {
        return this.allowCommands;
    }

    public boolean isInitialized()
    {
        return this.initialized;
    }

    public void setServerInitialized(boolean par1)
    {
        this.initialized = par1;
    }

    public GameRules getGameRulesInstance()
    {
        return this.theGameRules;
    }

    public void addToCrashReport(CrashReportCategory par1CrashReportCategory)
    {
        par1CrashReportCategory.addCrashSectionCallable("Level seed", new Callable()
        {
            private static final String __OBFID = "CL_00000588";
            public String call()
            {
                return String.valueOf(WorldInfo.this.getSeed());
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level generator", new Callable()
        {
            private static final String __OBFID = "CL_00000589";
            public String call()
            {
                return String.format("ID %02d - %s, ver %d. Features enabled: %b", new Object[] {Integer.valueOf(WorldInfo.this.terrainType.getWorldTypeID()), WorldInfo.this.terrainType.getWorldTypeName(), Integer.valueOf(WorldInfo.this.terrainType.getGeneratorVersion()), Boolean.valueOf(WorldInfo.this.mapFeaturesEnabled)});
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level generator options", new Callable()
        {
            private static final String __OBFID = "CL_00000590";
            public String call()
            {
                return WorldInfo.this.generatorOptions;
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level spawn location", new Callable()
        {
            private static final String __OBFID = "CL_00000591";
            public String call()
            {
                return CrashReportCategory.getLocationInfo(WorldInfo.this.spawnX, WorldInfo.this.spawnY, WorldInfo.this.spawnZ);
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level time", new Callable()
        {
            private static final String __OBFID = "CL_00000592";
            public String call()
            {
                return String.format("%d game time, %d day time", new Object[] {Long.valueOf(WorldInfo.this.totalTime), Long.valueOf(WorldInfo.this.worldTime)});
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level dimension", new Callable()
        {
            private static final String __OBFID = "CL_00000593";
            public String call()
            {
                return String.valueOf(WorldInfo.this.dimension);
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level storage version", new Callable()
        {
            private static final String __OBFID = "CL_00000594";
            public String call()
            {
                String var1 = "Unknown?";

                try
                {
                    switch (WorldInfo.this.saveVersion)
                    {
                        case 19132:
                            var1 = "McRegion";
                            break;

                        case 19133:
                            var1 = "Anvil";
                    }
                }
                catch (Throwable var3)
                {
                    ;
                }

                return String.format("0x%05X - %s", new Object[] {Integer.valueOf(WorldInfo.this.saveVersion), var1});
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level weather", new Callable()
        {
            private static final String __OBFID = "CL_00000595";
            public String call()
            {
                return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", new Object[] {Integer.valueOf(WorldInfo.this.rainTime), Boolean.valueOf(WorldInfo.this.raining), Integer.valueOf(WorldInfo.this.thunderTime), Boolean.valueOf(WorldInfo.this.thundering)});
            }
        });
        par1CrashReportCategory.addCrashSectionCallable("Level game mode", new Callable()
        {
            private static final String __OBFID = "CL_00000597";
            public String call()
            {
                return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", new Object[] {WorldInfo.this.theGameType.getName(), Integer.valueOf(WorldInfo.this.theGameType.getID()), Boolean.valueOf(WorldInfo.this.hardcore), Boolean.valueOf(WorldInfo.this.allowCommands)});
            }
        });
    }
}
