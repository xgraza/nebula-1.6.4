package minimap;

import net.minecraft.client.Minecraft;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.chunk.Chunk;
import us.nebula.api.listener.EventBus;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.impl.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 02/23/25
 */
public final class Minimap
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private static final int CHUNKS_TO_RENDER = 9;
    private static final int PIXELS_PER_BLOCK = 4;
    private static final int CHUNK_SIZE = 16;

    private static final int VIEW_DISTANCE_RANGE = CHUNKS_TO_RENDER * (PIXELS_PER_BLOCK * CHUNK_SIZE);

    private final ChunkPosition[] chunkPositions = new ChunkPosition[CHUNKS_TO_RENDER];
    private int lastChunkCoordX = -1, lastChunkCoordZ = -1;

    private final MinimapTexture texture;
    private boolean dirty;

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        final int chunkCoordX = MC.thePlayer.chunkCoordX;
        final int chunkCoordZ = MC.thePlayer.chunkCoordZ;

        if ((lastChunkCoordX == -1 && lastChunkCoordZ == -1)
                || (lastChunkCoordX != chunkCoordX || lastChunkCoordZ != chunkCoordZ))
        {
            lastChunkCoordX = chunkCoordX;
            lastChunkCoordZ = chunkCoordZ;
            searchSurroundingChunks();
            dirty = true;
            return;
        }

        if (dirty)
        {
            dirty = false;
            for (int i = 0; i < chunkPositions.length; ++i)
            {
                final ChunkPosition position = chunkPositions[i];
                final Chunk chunk = MC.theWorld.getChunkFromChunkCoords(
                        position.xCoord, position.yCoord);

                // getBlockStorageArray

                // we need to get each most toplevel block for the xz coords
                for (int x = 0; x < 16; ++x)
                {
                    for (int z = 0; z < 16; ++z)
                    {

                    }
                }
            }
        }

    };

    private void searchSurroundingChunks()
    {
        //   N
        // W   E
        //   S
        // x1 x2 x3
        // x4 pl x6
        // x7 x8 x9

        int i = 0;
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                chunkPositions[i++] = new ChunkPosition(
                        lastChunkCoordX + (x * CHUNK_SIZE),
                        0,
                        lastChunkCoordZ + (z * CHUNK_SIZE));
            }
        }
    }

    public Minimap()
    {
        EventBus.subscribe(this);
        texture = new MinimapTexture(VIEW_DISTANCE_RANGE, VIEW_DISTANCE_RANGE);
    }
}
