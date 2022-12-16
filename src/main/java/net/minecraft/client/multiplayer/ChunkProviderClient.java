package net.minecraft.client.multiplayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderClient implements IChunkProvider
{
    private static final Logger logger = LogManager.getLogger();
    private Chunk blankChunk;
    private LongHashMap chunkMapping = new LongHashMap();
    public List<Chunk> chunkListing = new ArrayList<>();
    private World worldObj;
    private static final String __OBFID = "CL_00000880";

    public ChunkProviderClient(World par1World)
    {
        this.blankChunk = new EmptyChunk(par1World, 0, 0);
        this.worldObj = par1World;
    }

    public boolean chunkExists(int par1, int par2)
    {
        return true;
    }

    public void unloadChunk(int par1, int par2)
    {
        Chunk var3 = this.provideChunk(par1, par2);

        if (!var3.isEmpty())
        {
            var3.onChunkUnload();
        }

        this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(par1, par2));
        this.chunkListing.remove(var3);
    }

    public Chunk loadChunk(int par1, int par2)
    {
        Chunk var3 = new Chunk(this.worldObj, par1, par2);
        this.chunkMapping.add(ChunkCoordIntPair.chunkXZ2Int(par1, par2), var3);
        this.chunkListing.add(var3);
        var3.isChunkLoaded = true;
        return var3;
    }

    public Chunk provideChunk(int par1, int par2)
    {
        Chunk var3 = (Chunk)this.chunkMapping.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(par1, par2));
        return var3 == null ? this.blankChunk : var3;
    }

    public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate)
    {
        return true;
    }

    public void saveExtraData() {}

    public boolean unloadQueuedChunks()
    {
        long var1 = System.currentTimeMillis();
        Iterator var3 = this.chunkListing.iterator();

        while (var3.hasNext())
        {
            Chunk var4 = (Chunk)var3.next();
            var4.func_150804_b(System.currentTimeMillis() - var1 > 5L);
        }

        if (System.currentTimeMillis() - var1 > 100L)
        {
            logger.info("Warning: Clientside chunk ticking took {} ms", new Object[] {Long.valueOf(System.currentTimeMillis() - var1)});
        }

        return false;
    }

    public boolean canSave()
    {
        return false;
    }

    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {}

    public String makeString()
    {
        return "MultiplayerChunkCache: " + this.chunkMapping.getNumHashElements() + ", " + this.chunkListing.size();
    }

    public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
    {
        return null;
    }

    public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_)
    {
        return null;
    }

    public int getLoadedChunkCount()
    {
        return this.chunkListing.size();
    }

    public void recreateStructures(int par1, int par2) {}
}
