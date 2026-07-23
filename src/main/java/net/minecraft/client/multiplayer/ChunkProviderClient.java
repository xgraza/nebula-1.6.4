package net.minecraft.client.multiplayer;

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

import java.util.ArrayList;
import java.util.List;

public class ChunkProviderClient implements IChunkProvider
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The completely empty chunk used by ChunkProviderClient when chunkMapping doesn't contain the requested
     * coordinates.
     */
    private final Chunk blankChunk;

    /**
     * The mapping between ChunkCoordinates and Chunks that ChunkProviderClient maintains.
     */
    private final LongHashMap<Chunk> chunkMapping = new LongHashMap<>();

    /**
     * This may have been intended to be an iterable version of all currently loaded chunks (MultiplayerChunkCache),
     * with identical contents to chunkMapping's values. However it is never actually added to.
     */
    private final List<Chunk> chunkListing = new ArrayList<>();

    /**
     * Reference to the World object.
     */
    private final World worldObj;

    public ChunkProviderClient(World world)
    {
        this.blankChunk = new EmptyChunk(world, 0, 0);
        this.worldObj = world;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int x, int y)
    {
        return true;
    }

    /**
     * Unload chunk from ChunkProviderClient's hashmap. Called in response to a Packet50PreChunk with its mode field set
     * to false
     */
    public void unloadChunk(int x, int z)
    {
        Chunk chunk = this.provideChunk(x, z);

        if (!chunk.isEmpty())
        {
            chunk.onChunkUnload();
        }

        this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(x, z));
        this.chunkListing.remove(chunk);
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk(int x, int z)
    {
        Chunk chunk = new Chunk(this.worldObj, x, z);
        this.chunkMapping.add(ChunkCoordIntPair.chunkXZ2Int(x, z), chunk);
        this.chunkListing.add(chunk);
        chunk.isChunkLoaded = true;
        return chunk;
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int x, int z)
    {
        Chunk chunk = this.chunkMapping.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x, z));
        return chunk == null ? this.blankChunk : chunk;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean saved, IProgressUpdate updater)
    {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData()
    {
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks()
    {
        long nowMs = System.currentTimeMillis();

        for (Chunk chunk : this.chunkListing)
        {
            chunk.func_150804_b(System.currentTimeMillis() - nowMs > 5L);
        }

        if (System.currentTimeMillis() - nowMs > 100L)
        {
            LOGGER.info("Warning: Clientside chunk ticking took {} ms", System.currentTimeMillis() - nowMs);
        }

        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return false;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3)
    {
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "MultiplayerChunkCache: " + this.chunkMapping.getNumHashElements() + ", " + this.chunkListing.size();
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
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

    public LongHashMap<Chunk> getChunkMapping()
    {
        return chunkMapping;
    }

    public List<Chunk> getChunkListing()
    {
        return chunkListing;
    }

    public void recreateStructures(int par1, int par2)
    {
    }
}
