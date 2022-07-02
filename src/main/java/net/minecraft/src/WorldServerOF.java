package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.server.MinecraftServer;

public class WorldServerOF extends WorldServer
{
    private NextTickHashSet nextTickHashSet = null;
    private TreeSet pendingTickList = null;

    public WorldServerOF(MinecraftServer par1MinecraftServer, ISaveHandler par2iSaveHandler, String par3Str, int par4, WorldSettings par5WorldSettings, Profiler par6Profiler, ILogAgent par7ILogAgent)
    {
        super(par1MinecraftServer, par2iSaveHandler, par3Str, par4, par5WorldSettings, par6Profiler, par7ILogAgent);
        this.fixSetNextTicks();
    }

    private void fixSetNextTicks()
    {
        try
        {
            Field[] e = WorldServer.class.getDeclaredFields();

            if (e.length > 5)
            {
                Field f = e[3];
                f.setAccessible(true);

                if (f.getType() == Set.class)
                {
                    Set oldSet = (Set)f.get(this);
                    NextTickHashSet newSet = new NextTickHashSet(oldSet);
                    f.set(this, newSet);
                    Field f2 = e[4];
                    f2.setAccessible(true);
                    this.pendingTickList = (TreeSet)f2.get(this);
                    this.nextTickHashSet = newSet;
                }
            }
        }
        catch (Exception var6)
        {
            Config.warn("Error setting WorldServer.nextTickSet: " + var6.getMessage());
        }
    }

    public List getPendingBlockUpdates(Chunk par1Chunk, boolean par2)
    {
        if (this.nextTickHashSet != null && this.pendingTickList != null)
        {
            ArrayList var3 = null;
            ChunkCoordIntPair var4 = par1Chunk.getChunkCoordIntPair();
            int var5 = var4.chunkXPos << 4;
            int var6 = var5 + 16;
            int var7 = var4.chunkZPos << 4;
            int var8 = var7 + 16;
            Iterator var9 = this.nextTickHashSet.getNextTickEntries(var4.chunkXPos, var4.chunkZPos);

            while (var9.hasNext())
            {
                NextTickListEntry var10 = (NextTickListEntry)var9.next();

                if (var10.xCoord >= var5 && var10.xCoord < var6 && var10.zCoord >= var7 && var10.zCoord < var8)
                {
                    if (par2)
                    {
                        this.pendingTickList.remove(var10);
                        var9.remove();
                    }

                    if (var3 == null)
                    {
                        var3 = new ArrayList();
                    }

                    var3.add(var10);
                }
                else
                {
                    Config.warn("Not matching: " + var5 + "," + var7);
                }
            }

            return var3;
        }
        else
        {
            return super.getPendingBlockUpdates(par1Chunk, par2);
        }
    }

    /**
     * Runs a single tick for the world
     */
    public void tick()
    {
        super.tick();

        if (!Config.isTimeDefault())
        {
            this.fixWorldTime();
        }

        if (Config.waterOpacityChanged)
        {
            Config.waterOpacityChanged = false;
            this.updateWaterOpacity();
        }
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather()
    {
        if (Config.isWeatherEnabled())
        {
            super.updateWeather();
        }
        else
        {
            this.fixWorldWeather();
        }
    }

    private void fixWorldWeather()
    {
        if (this.worldInfo.isRaining() || this.worldInfo.isThundering())
        {
            this.worldInfo.setRainTime(0);
            this.worldInfo.setRaining(false);
            this.setRainStrength(0.0F);
            this.worldInfo.setThunderTime(0);
            this.worldInfo.setThundering(false);
            this.getMinecraftServer().getConfigurationManager().sendPacketToAllPlayers(new Packet70GameEvent(2, 0));
        }
    }

    private void fixWorldTime()
    {
        if (this.worldInfo.getGameType().getID() == 1)
        {
            long time = this.getWorldTime();
            long timeOfDay = time % 24000L;

            if (Config.isTimeDayOnly())
            {
                if (timeOfDay <= 1000L)
                {
                    this.setWorldTime(time - timeOfDay + 1001L);
                }

                if (timeOfDay >= 11000L)
                {
                    this.setWorldTime(time - timeOfDay + 24001L);
                }
            }

            if (Config.isTimeNightOnly())
            {
                if (timeOfDay <= 14000L)
                {
                    this.setWorldTime(time - timeOfDay + 14001L);
                }

                if (timeOfDay >= 22000L)
                {
                    this.setWorldTime(time - timeOfDay + 24000L + 14001L);
                }
            }
        }
    }

    public void updateWaterOpacity()
    {
        byte opacity = 3;

        if (Config.isClearWater())
        {
            opacity = 1;
        }

        Block.waterStill.setLightOpacity(opacity);
        Block.waterMoving.setLightOpacity(opacity);
        IChunkProvider cp = this.chunkProvider;

        if (cp != null)
        {
            for (int x = -512; x < 512; ++x)
            {
                for (int z = -512; z < 512; ++z)
                {
                    if (cp.chunkExists(x, z))
                    {
                        Chunk c = cp.provideChunk(x, z);

                        if (c != null && !(c instanceof EmptyChunk))
                        {
                            ExtendedBlockStorage[] ebss = c.getBlockStorageArray();

                            for (int i = 0; i < ebss.length; ++i)
                            {
                                ExtendedBlockStorage ebs = ebss[i];

                                if (ebs != null)
                                {
                                    NibbleArray na = ebs.getSkylightArray();

                                    if (na != null)
                                    {
                                        byte[] data = na.data;

                                        for (int d = 0; d < data.length; ++d)
                                        {
                                            data[d] = 0;
                                        }
                                    }
                                }
                            }

                            c.generateSkylightMap();
                        }
                    }
                }
            }
        }
    }
}
