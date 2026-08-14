package ez.nebula.client.api.listener.event.world;

import ez.nebula.client.api.listener.Event;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public final class EventUnloadChunk extends Event
{
    private final Chunk chunk;
    private final IChunkProvider chunkProvider;

    public EventUnloadChunk(Chunk chunk, IChunkProvider chunkProvider)
    {
        this.chunk = chunk;
        this.chunkProvider = chunkProvider;
    }

    public Chunk getChunk()
    {
        return chunk;
    }

    public IChunkProvider getChunkProvider()
    {
        return chunkProvider;
    }
}
