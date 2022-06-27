package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

class PlayerInstance
{
    private final List playersInChunk;

    /** note: this is final */
    private final ChunkCoordIntPair chunkLocation;
    private short[] locationOfBlockChange;
    private int numberOfTilesToUpdate;

    /**
     * Integer field where each bit means to make update 16x16x16 division of chunk (from bottom).
     */
    private int flagsYAreasToUpdate;

    /** time what is using when chunk InhabitedTime is being calculated */
    private long previousWorldTime;

    final PlayerManager thePlayerManager;

    public PlayerInstance(PlayerManager par1PlayerManager, int par2, int par3)
    {
        this.thePlayerManager = par1PlayerManager;
        this.playersInChunk = new ArrayList();
        this.locationOfBlockChange = new short[64];
        this.chunkLocation = new ChunkCoordIntPair(par2, par3);
        par1PlayerManager.getWorldServer().theChunkProviderServer.loadChunk(par2, par3);
    }

    public void addPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        if (this.playersInChunk.contains(par1EntityPlayerMP))
        {
            throw new IllegalStateException("Failed to add player. " + par1EntityPlayerMP + " already is in chunk " + this.chunkLocation.chunkXPos + ", " + this.chunkLocation.chunkZPos);
        }
        else
        {
            if (this.playersInChunk.isEmpty())
            {
                this.previousWorldTime = PlayerManager.getWorldServer(this.thePlayerManager).getTotalWorldTime();
            }

            this.playersInChunk.add(par1EntityPlayerMP);
            par1EntityPlayerMP.loadedChunks.add(this.chunkLocation);
        }
    }

    public void removePlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        if (this.playersInChunk.contains(par1EntityPlayerMP))
        {
            Chunk var2 = PlayerManager.getWorldServer(this.thePlayerManager).getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos);
            par1EntityPlayerMP.playerNetServerHandler.sendPacketToPlayer(new Packet51MapChunk(var2, true, 0));
            this.playersInChunk.remove(par1EntityPlayerMP);
            par1EntityPlayerMP.loadedChunks.remove(this.chunkLocation);

            if (this.playersInChunk.isEmpty())
            {
                long var3 = (long)this.chunkLocation.chunkXPos + 2147483647L | (long)this.chunkLocation.chunkZPos + 2147483647L << 32;
                this.increaseInhabitedTime(var2);
                PlayerManager.getChunkWatchers(this.thePlayerManager).remove(var3);
                PlayerManager.getChunkWatcherList(this.thePlayerManager).remove(this);

                if (this.numberOfTilesToUpdate > 0)
                {
                    PlayerManager.getChunkWatchersWithPlayers(this.thePlayerManager).remove(this);
                }

                this.thePlayerManager.getWorldServer().theChunkProviderServer.unloadChunksIfNotNearSpawn(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos);
            }
        }
    }

    /**
     * This method currently only increases chunk inhabited time. Extension is possible in next versions
     */
    public void processChunk()
    {
        this.increaseInhabitedTime(PlayerManager.getWorldServer(this.thePlayerManager).getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos));
    }

    /**
     * Increases chunk inhabited time every 8000 ticks
     */
    private void increaseInhabitedTime(Chunk par1Chunk)
    {
        par1Chunk.inhabitedTime += PlayerManager.getWorldServer(this.thePlayerManager).getTotalWorldTime() - this.previousWorldTime;
        this.previousWorldTime = PlayerManager.getWorldServer(this.thePlayerManager).getTotalWorldTime();
    }

    public void flagChunkForUpdate(int par1, int par2, int par3)
    {
        if (this.numberOfTilesToUpdate == 0)
        {
            PlayerManager.getChunkWatchersWithPlayers(this.thePlayerManager).add(this);
        }

        this.flagsYAreasToUpdate |= 1 << (par2 >> 4);

        if (this.numberOfTilesToUpdate < 64)
        {
            short var4 = (short)(par1 << 12 | par3 << 8 | par2);

            for (int var5 = 0; var5 < this.numberOfTilesToUpdate; ++var5)
            {
                if (this.locationOfBlockChange[var5] == var4)
                {
                    return;
                }
            }

            this.locationOfBlockChange[this.numberOfTilesToUpdate++] = var4;
        }
    }

    public void sendToAllPlayersWatchingChunk(Packet par1Packet)
    {
        for (int var2 = 0; var2 < this.playersInChunk.size(); ++var2)
        {
            EntityPlayerMP var3 = (EntityPlayerMP)this.playersInChunk.get(var2);

            if (!var3.loadedChunks.contains(this.chunkLocation))
            {
                var3.playerNetServerHandler.sendPacketToPlayer(par1Packet);
            }
        }
    }

    public void sendChunkUpdate()
    {
        if (this.numberOfTilesToUpdate != 0)
        {
            int var1;
            int var2;
            int var3;

            if (this.numberOfTilesToUpdate == 1)
            {
                var1 = this.chunkLocation.chunkXPos * 16 + (this.locationOfBlockChange[0] >> 12 & 15);
                var2 = this.locationOfBlockChange[0] & 255;
                var3 = this.chunkLocation.chunkZPos * 16 + (this.locationOfBlockChange[0] >> 8 & 15);
                this.sendToAllPlayersWatchingChunk(new Packet53BlockChange(var1, var2, var3, PlayerManager.getWorldServer(this.thePlayerManager)));

                if (PlayerManager.getWorldServer(this.thePlayerManager).blockHasTileEntity(var1, var2, var3))
                {
                    this.sendTileToAllPlayersWatchingChunk(PlayerManager.getWorldServer(this.thePlayerManager).getBlockTileEntity(var1, var2, var3));
                }
            }
            else
            {
                int var4;

                if (this.numberOfTilesToUpdate == 64)
                {
                    var1 = this.chunkLocation.chunkXPos * 16;
                    var2 = this.chunkLocation.chunkZPos * 16;
                    this.sendToAllPlayersWatchingChunk(new Packet51MapChunk(PlayerManager.getWorldServer(this.thePlayerManager).getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos), false, this.flagsYAreasToUpdate));

                    for (var3 = 0; var3 < 16; ++var3)
                    {
                        if ((this.flagsYAreasToUpdate & 1 << var3) != 0)
                        {
                            var4 = var3 << 4;
                            List var5 = PlayerManager.getWorldServer(this.thePlayerManager).getAllTileEntityInBox(var1, var4, var2, var1 + 16, var4 + 16, var2 + 16);

                            for (int var6 = 0; var6 < var5.size(); ++var6)
                            {
                                this.sendTileToAllPlayersWatchingChunk((TileEntity)var5.get(var6));
                            }
                        }
                    }
                }
                else
                {
                    this.sendToAllPlayersWatchingChunk(new Packet52MultiBlockChange(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos, this.locationOfBlockChange, this.numberOfTilesToUpdate, PlayerManager.getWorldServer(this.thePlayerManager)));

                    for (var1 = 0; var1 < this.numberOfTilesToUpdate; ++var1)
                    {
                        var2 = this.chunkLocation.chunkXPos * 16 + (this.locationOfBlockChange[var1] >> 12 & 15);
                        var3 = this.locationOfBlockChange[var1] & 255;
                        var4 = this.chunkLocation.chunkZPos * 16 + (this.locationOfBlockChange[var1] >> 8 & 15);

                        if (PlayerManager.getWorldServer(this.thePlayerManager).blockHasTileEntity(var2, var3, var4))
                        {
                            this.sendTileToAllPlayersWatchingChunk(PlayerManager.getWorldServer(this.thePlayerManager).getBlockTileEntity(var2, var3, var4));
                        }
                    }
                }
            }

            this.numberOfTilesToUpdate = 0;
            this.flagsYAreasToUpdate = 0;
        }
    }

    private void sendTileToAllPlayersWatchingChunk(TileEntity par1TileEntity)
    {
        if (par1TileEntity != null)
        {
            Packet var2 = par1TileEntity.getDescriptionPacket();

            if (var2 != null)
            {
                this.sendToAllPlayersWatchingChunk(var2);
            }
        }
    }

    static ChunkCoordIntPair getChunkLocation(PlayerInstance par0PlayerInstance)
    {
        return par0PlayerInstance.chunkLocation;
    }

    static List getPlayersInChunk(PlayerInstance par0PlayerInstance)
    {
        return par0PlayerInstance.playersInChunk;
    }
}
