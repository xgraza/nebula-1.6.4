package ez.nebula.client.api.world;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xgraza
 * @since 6/4/26
 */
public final class BlockSearcher extends Thread
{
    private static final AtomicInteger THREAD_ID = new AtomicInteger(0);
    private static final Minecraft MC = Minecraft.getMinecraft();

    private final Set<Block> searchBlockList = new ConcurrentSet<>();
    private final Set<BlockPos> foundPositionList = new ConcurrentSet<>();
    private int searchRange;
    private boolean searching;

    public BlockSearcher()
    {
        setName("Block Searcher Thread #" + THREAD_ID.incrementAndGet());
        setDaemon(true);
        start();
    }

    @Override
    public void run()
    {
        while (!isInterrupted())
        {
            if (!searching || searchRange <= 0)
            {
                continue;
            }
            final World world = MC.theWorld;
            final EntityPlayer player = MC.thePlayer;
            if (player == null || world == null)
            {
                continue;
            }

            final int baseChunkX = (int)Math.floor(player.posX);
            final int baseChunkZ = (int)Math.floor(player.posZ);
            for (int y = 0; y < 256; ++y)
            {
                if (!searching)
                {
                    break;
                }
                for (int x = -searchRange; x <= searchRange; ++x)
                {
                    for (int z = -searchRange; z <= searchRange; ++z)
                    {
                        final Chunk chunk = world.getChunkFromChunkCoords((baseChunkX + (x * 16)) >> 4, (baseChunkZ + (z * 16)) >> 4);
                        if (chunk == null)
                        {
                            continue;
                        }

                        searchChunk(chunk, y);
                    }
                }
            }
        }
    }

    private void searchChunk(final Chunk chunk, final int y)
    {
        if (chunk == null || !searching)
        {
            return;
        }
        final ExtendedBlockStorage[] blockStorageArray = chunk.getBlockStorageArray();
        if (blockStorageArray.length > y >> 4)
        {
            final ExtendedBlockStorage blockStorage = blockStorageArray[y >> 4];
            if (blockStorage == null)
            {
                return;
            }

            final int chunkX = chunk.xPosition;
            final int chunkZ = chunk.zPosition;

            for (int x = 0; x <= 16; ++x)
            {
                for (int z = 0; z <= 16; ++z)
                {
                    if (!searching)
                    {
                        break;
                    }
                    final int posX = ((chunkX * 16) + x);
                    final int posZ = ((chunkZ * 16) + z);
                    final Block block = blockStorage.func_150819_a(posX & 15, y & 15, posZ & 15);
                    if (block == null)
                    {
                        continue;
                    }

                    for (final Block b : searchBlockList)
                    {
                        if (b == block)
                        {
                            foundPositionList.add(new BlockPos(posX, y, posZ));
                        }
                    }
                }
            }
        }
    }

    public void addSearchBlocks(final Block... blocks)
    {
        searchBlockList.addAll(Arrays.asList(blocks));
        foundPositionList.clear();
    }

    public void removeSearchBlocks(final Block... blocks)
    {
        for (final Block block : blocks)
        {
            searchBlockList.remove(block);
        }
        foundPositionList.clear();
    }

    public Set<BlockPos> getFoundPositionList()
    {
        return foundPositionList;
    }

    public void setSearchRange(int searchRange)
    {
        this.searchRange = searchRange;
    }

    public void setSearching(boolean searching)
    {
        foundPositionList.clear();
        this.searching = searching;
        if (!searching)
        {
            searchRange = 0;
        }
    }

    public boolean isSearching()
    {
        return searching;
    }
}
