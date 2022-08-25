package net.minecraft.world.chunk;

import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public interface IChunkProvider
{
    boolean chunkExists(int var1, int var2);

    Chunk provideChunk(int var1, int var2);

    Chunk loadChunk(int var1, int var2);

    void populate(IChunkProvider var1, int var2, int var3);

    boolean saveChunks(boolean var1, IProgressUpdate var2);

    boolean unloadQueuedChunks();

    boolean canSave();

    String makeString();

    List getPossibleCreatures(EnumCreatureType var1, int var2, int var3, int var4);

    ChunkPosition func_147416_a(World var1, String var2, int var3, int var4, int var5);

    int getLoadedChunkCount();

    void recreateStructures(int var1, int var2);

    void saveExtraData();
}
