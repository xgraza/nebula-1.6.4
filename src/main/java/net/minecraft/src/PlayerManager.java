package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager
{
    private final WorldServer theWorldServer;

    /** players in the current instance */
    private final List players = new ArrayList();

    /**
     * A map of chunk position (two ints concatenated into a long) to PlayerInstance
     */
    private final LongHashMap playerInstances = new LongHashMap();

    /**
     * contains a PlayerInstance for every chunk they can see. the "player instance" cotains a list of all players who
     * can also that chunk
     */
    private final List chunkWatcherWithPlayers = new ArrayList();

    /** This field is using when chunk should be processed (every 8000 ticks) */
    private final List playerInstanceList = new ArrayList();
    public CompactArrayList chunkCoordsNotLoaded = new CompactArrayList(100, 0.8F);

    /**
     * Number of chunks the server sends to the client. Valid 3<=x<=15. In server.properties.
     */
    private int playerViewRadius;

    /** time what is using to check if InhabitedTime should be calculated */
    private long previousTotalWorldTime;

    /** x, z direction vectors: east, south, west, north */
    private final int[][] xzDirectionsConst = new int[][] {{1, 0}, {0, 1}, { -1, 0}, {0, -1}};

    public PlayerManager(WorldServer par1WorldServer, int par2)
    {
        if (par2 > 15)
        {
            throw new IllegalArgumentException("Too big view radius!");
        }
        else if (par2 < 3)
        {
            throw new IllegalArgumentException("Too small view radius!");
        }
        else
        {
            this.playerViewRadius = Config.getChunkViewDistance();
            Config.dbg("ViewRadius: " + this.playerViewRadius + ", for: " + this + " (constructor)");
            this.theWorldServer = par1WorldServer;
        }
    }

    public WorldServer getWorldServer()
    {
        return this.theWorldServer;
    }

    /**
     * updates all the player instances that need to be updated
     */
    public void updatePlayerInstances()
    {
        long var1 = this.theWorldServer.getTotalWorldTime();
        int var3;
        PlayerInstance var4;

        if (var1 - this.previousTotalWorldTime > 8000L)
        {
            this.previousTotalWorldTime = var1;

            for (var3 = 0; var3 < this.playerInstanceList.size(); ++var3)
            {
                var4 = (PlayerInstance)this.playerInstanceList.get(var3);
                var4.sendChunkUpdate();
                var4.processChunk();
            }
        }
        else
        {
            for (var3 = 0; var3 < this.chunkWatcherWithPlayers.size(); ++var3)
            {
                var4 = (PlayerInstance)this.chunkWatcherWithPlayers.get(var3);
                var4.sendChunkUpdate();
            }
        }

        this.chunkWatcherWithPlayers.clear();

        if (this.players.isEmpty())
        {
            WorldProvider var5 = this.theWorldServer.provider;

            if (!var5.canRespawnHere())
            {
                this.theWorldServer.theChunkProviderServer.unloadAllChunks();
            }
        }

        if (this.playerViewRadius != Config.getChunkViewDistance())
        {
            this.setPlayerViewRadius(Config.getChunkViewDistance());
        }

        if (this.chunkCoordsNotLoaded.size() > 0)
        {
            for (int var22 = 0; var22 < this.players.size(); ++var22)
            {
                EntityPlayerMP var6 = (EntityPlayerMP)this.players.get(var22);
                int var7 = var6.chunkCoordX;
                int var8 = var6.chunkCoordZ;
                int var9 = this.playerViewRadius + 1;
                int var10 = var9 / 2;
                int var11 = var9 * var9 + var10 * var10;
                int var12 = var11;
                int var13 = -1;
                PlayerInstance var14 = null;
                ChunkCoordIntPair var15 = null;

                for (int var16 = 0; var16 < this.chunkCoordsNotLoaded.size(); ++var16)
                {
                    ChunkCoordIntPair var17 = (ChunkCoordIntPair)this.chunkCoordsNotLoaded.get(var16);

                    if (var17 != null)
                    {
                        PlayerInstance var18 = this.getOrCreateChunkWatcher(var17.chunkXPos, var17.chunkZPos, false);

                        if (var18 != null && !var18.chunkLoaded)
                        {
                            int var19 = var7 - var17.chunkXPos;
                            int var20 = var8 - var17.chunkZPos;
                            int var21 = var19 * var19 + var20 * var20;

                            if (var21 < var12)
                            {
                                var12 = var21;
                                var13 = var16;
                                var14 = var18;
                                var15 = var17;
                            }
                        }
                        else
                        {
                            this.chunkCoordsNotLoaded.set(var16, (Object)null);
                        }
                    }
                }

                if (var13 >= 0)
                {
                    this.chunkCoordsNotLoaded.set(var13, (Object)null);
                }

                if (var14 != null)
                {
                    var14.chunkLoaded = true;
                    this.getWorldServer().theChunkProviderServer.loadChunk(var15.chunkXPos, var15.chunkZPos);
                    var14.sendThisChunkToAllPlayers();
                    break;
                }
            }

            this.chunkCoordsNotLoaded.compact();
        }
    }

    public PlayerInstance getOrCreateChunkWatcher(int par1, int par2, boolean par3)
    {
        return this.getOrCreateChunkWatcher(par1, par2, par3, false);
    }

    public PlayerInstance getOrCreateChunkWatcher(int par1, int par2, boolean par3, boolean lazy)
    {
        long var4 = (long)par1 + 2147483647L | (long)par2 + 2147483647L << 32;
        PlayerInstance var6 = (PlayerInstance)this.playerInstances.getValueByKey(var4);

        if (var6 == null && par3)
        {
            var6 = new PlayerInstance(this, par1, par2, lazy);
            this.playerInstances.add(var4, var6);
            this.playerInstanceList.add(var6);
        }

        return var6;
    }

    /**
     * Called by WorldManager.markBlockForUpdate; marks a block to be resent to clients.
     */
    public void markBlockForUpdate(int par1, int par2, int par3)
    {
        int var4 = par1 >> 4;
        int var5 = par3 >> 4;
        PlayerInstance var6 = this.getOrCreateChunkWatcher(var4, var5, false);

        if (var6 != null)
        {
            var6.flagChunkForUpdate(par1 & 15, par2, par3 & 15);
        }
    }

    /**
     * Adds an EntityPlayerMP to the PlayerManager.
     */
    public void addPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int var2 = (int)par1EntityPlayerMP.posX >> 4;
        int var3 = (int)par1EntityPlayerMP.posZ >> 4;
        par1EntityPlayerMP.managedPosX = par1EntityPlayerMP.posX;
        par1EntityPlayerMP.managedPosZ = par1EntityPlayerMP.posZ;
        ArrayList var4 = new ArrayList(1);

        for (int var5 = var2 - this.playerViewRadius; var5 <= var2 + this.playerViewRadius; ++var5)
        {
            for (int var6 = var3 - this.playerViewRadius; var6 <= var3 + this.playerViewRadius; ++var6)
            {
                this.getOrCreateChunkWatcher(var5, var6, true).addPlayer(par1EntityPlayerMP);

                if (var5 >= var2 - 1 && var5 <= var2 + 1 && var6 >= var3 - 1 && var6 <= var3 + 1)
                {
                    Chunk var7 = this.getWorldServer().theChunkProviderServer.loadChunk(var5, var6);
                    var4.add(var7);
                }
            }
        }

        par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet56MapChunks(var4));
        this.players.add(par1EntityPlayerMP);
        this.filterChunkLoadQueue(par1EntityPlayerMP);
    }

    /**
     * Removes all chunks from the given player's chunk load queue that are not in viewing range of the player.
     */
    public void filterChunkLoadQueue(EntityPlayerMP par1EntityPlayerMP)
    {
        ArrayList var2 = new ArrayList(par1EntityPlayerMP.loadedChunks);
        int var3 = 0;
        int var4 = this.playerViewRadius;
        int var5 = (int)par1EntityPlayerMP.posX >> 4;
        int var6 = (int)par1EntityPlayerMP.posZ >> 4;
        int var7 = 0;
        int var8 = 0;
        ChunkCoordIntPair var9 = PlayerInstance.getChunkLocation(this.getOrCreateChunkWatcher(var5, var6, true));
        par1EntityPlayerMP.loadedChunks.clear();

        if (var2.contains(var9))
        {
            par1EntityPlayerMP.loadedChunks.add(var9);
        }

        int var10;

        for (var10 = 1; var10 <= var4 * 2; ++var10)
        {
            for (int var11 = 0; var11 < 2; ++var11)
            {
                int[] var12 = this.xzDirectionsConst[var3++ % 4];

                for (int var13 = 0; var13 < var10; ++var13)
                {
                    var7 += var12[0];
                    var8 += var12[1];
                    var9 = PlayerInstance.getChunkLocation(this.getOrCreateChunkWatcher(var5 + var7, var6 + var8, true));

                    if (var2.contains(var9))
                    {
                        par1EntityPlayerMP.loadedChunks.add(var9);
                    }
                }
            }
        }

        var3 %= 4;

        for (var10 = 0; var10 < var4 * 2; ++var10)
        {
            var7 += this.xzDirectionsConst[var3][0];
            var8 += this.xzDirectionsConst[var3][1];
            var9 = PlayerInstance.getChunkLocation(this.getOrCreateChunkWatcher(var5 + var7, var6 + var8, true));

            if (var2.contains(var9))
            {
                par1EntityPlayerMP.loadedChunks.add(var9);
            }
        }
    }

    /**
     * Removes an EntityPlayerMP from the PlayerManager.
     */
    public void removePlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int var2 = (int)par1EntityPlayerMP.managedPosX >> 4;
        int var3 = (int)par1EntityPlayerMP.managedPosZ >> 4;

        for (int var4 = var2 - this.playerViewRadius; var4 <= var2 + this.playerViewRadius; ++var4)
        {
            for (int var5 = var3 - this.playerViewRadius; var5 <= var3 + this.playerViewRadius; ++var5)
            {
                PlayerInstance var6 = this.getOrCreateChunkWatcher(var4, var5, false);

                if (var6 != null)
                {
                    var6.removePlayer(par1EntityPlayerMP, false);
                }
            }
        }

        this.players.remove(par1EntityPlayerMP);
    }

    /**
     * Determine if two rectangles centered at the given points overlap for the provided radius. Arguments: x1, z1, x2,
     * z2, radius.
     */
    private boolean overlaps(int par1, int par2, int par3, int par4, int par5)
    {
        int var6 = par1 - par3;
        int var7 = par2 - par4;
        return var6 >= -par5 && var6 <= par5 ? var7 >= -par5 && var7 <= par5 : false;
    }

    /**
     * update chunks around a player being moved by server logic (e.g. cart, boat)
     */
    public void updateMountedMovingPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int var2 = (int)par1EntityPlayerMP.posX >> 4;
        int var3 = (int)par1EntityPlayerMP.posZ >> 4;
        double var4 = par1EntityPlayerMP.managedPosX - par1EntityPlayerMP.posX;
        double var6 = par1EntityPlayerMP.managedPosZ - par1EntityPlayerMP.posZ;
        double var8 = var4 * var4 + var6 * var6;

        if (var8 >= 64.0D)
        {
            int var10 = (int)par1EntityPlayerMP.managedPosX >> 4;
            int var11 = (int)par1EntityPlayerMP.managedPosZ >> 4;
            int var12 = this.playerViewRadius;
            int var13 = var2 - var10;
            int var14 = var3 - var11;

            if (var13 != 0 || var14 != 0)
            {
                for (int var15 = var2 - var12; var15 <= var2 + var12; ++var15)
                {
                    for (int var16 = var3 - var12; var16 <= var3 + var12; ++var16)
                    {
                        if (!this.overlaps(var15, var16, var10, var11, var12))
                        {
                            this.getOrCreateChunkWatcher(var15, var16, true, true).addPlayer(par1EntityPlayerMP);
                        }

                        if (!this.overlaps(var15 - var13, var16 - var14, var2, var3, var12))
                        {
                            PlayerInstance var17 = this.getOrCreateChunkWatcher(var15 - var13, var16 - var14, false);

                            if (var17 != null)
                            {
                                var17.removePlayer(par1EntityPlayerMP);
                            }
                        }
                    }
                }

                this.filterChunkLoadQueue(par1EntityPlayerMP);
                par1EntityPlayerMP.managedPosX = par1EntityPlayerMP.posX;
                par1EntityPlayerMP.managedPosZ = par1EntityPlayerMP.posZ;
            }
        }
    }

    public boolean isPlayerWatchingChunk(EntityPlayerMP par1EntityPlayerMP, int par2, int par3)
    {
        PlayerInstance var4 = this.getOrCreateChunkWatcher(par2, par3, false);
        return var4 == null ? false : PlayerInstance.getPlayersInChunk(var4).contains(par1EntityPlayerMP) && !par1EntityPlayerMP.loadedChunks.contains(PlayerInstance.getChunkLocation(var4));
    }

    /**
     * Get the furthest viewable block given player's view distance
     */
    public static int getFurthestViewableBlock(int par0)
    {
        return par0 * 16 - 16;
    }

    static WorldServer getWorldServer(PlayerManager par0PlayerManager)
    {
        return par0PlayerManager.theWorldServer;
    }

    static LongHashMap getChunkWatchers(PlayerManager par0PlayerManager)
    {
        return par0PlayerManager.playerInstances;
    }

    /**
     * Returns player instances as list
     */
    static List getChunkWatcherList(PlayerManager par0PlayerManager)
    {
        return par0PlayerManager.playerInstanceList;
    }

    static List getChunkWatchersWithPlayers(PlayerManager par0PlayerManager)
    {
        return par0PlayerManager.chunkWatcherWithPlayers;
    }

    private void setPlayerViewRadius(int newRadius)
    {
        if (this.playerViewRadius != newRadius)
        {
            EntityPlayerMP[] eps = (EntityPlayerMP[])((EntityPlayerMP[])this.players.toArray(new EntityPlayerMP[this.players.size()]));
            int i;
            EntityPlayerMP ep;

            for (i = 0; i < eps.length; ++i)
            {
                ep = eps[i];
                this.removePlayer(ep);
            }

            this.playerViewRadius = newRadius;

            for (i = 0; i < eps.length; ++i)
            {
                ep = eps[i];
                this.addPlayer(ep);
            }

            Config.dbg("ViewRadius: " + this.playerViewRadius + ", for: " + this + " (detect)");
        }
    }
}
