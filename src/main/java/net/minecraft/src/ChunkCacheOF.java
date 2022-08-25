package net.minecraft.src;

import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ChunkCacheOF extends ChunkCache
{
    private IBlockAccess blockAccess;
    private BlockPos position;
    private int[] combinedLights;
    private Block[] blocks;
    private static ArrayCache cacheCombinedLights = new ArrayCache(Integer.TYPE, 16);
    private static ArrayCache cacheBlocks = new ArrayCache(Block.class, 16);
    private static final int ARRAY_SIZE = 8000;

    public ChunkCacheOF(World world, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, int subIn)
    {
        super(world, xMin, yMin, zMin, xMax, yMax, zMax, subIn);
        this.blockAccess = world;
        this.position = new BlockPos(xMin - subIn, yMin - subIn, zMin - subIn);
    }

    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int lightValue)
    {
        if (this.combinedLights == null)
        {
            return this.getLightBrightnessForSkyBlocksRaw(x, y, z, lightValue);
        }
        else
        {
            int index = this.getPositionIndex(x, y, z);

            if (index >= 0 && index < this.combinedLights.length)
            {
                int light = this.combinedLights[index];

                if (light == -1)
                {
                    light = this.getLightBrightnessForSkyBlocksRaw(x, y, z, lightValue);
                    this.combinedLights[index] = light;
                }

                return light;
            }
            else
            {
                return this.getLightBrightnessForSkyBlocksRaw(x, y, z, lightValue);
            }
        }
    }

    private int getLightBrightnessForSkyBlocksRaw(int x, int y, int z, int lightValue)
    {
        int light = this.blockAccess.getLightBrightnessForSkyBlocks(x, y, z, lightValue);

        if (Config.isDynamicLights() && !this.getBlock(x, y, z).isOpaqueCube())
        {
            light = DynamicLights.getCombinedLight(x, y, z, light);
        }

        return light;
    }

    public Block getBlock(int x, int y, int z)
    {
        if (this.blocks == null)
        {
            return this.blockAccess.getBlock(x, y, z);
        }
        else
        {
            int index = this.getPositionIndex(x, y, z);

            if (index >= 0 && index < this.blocks.length)
            {
                Block block = this.blocks[index];

                if (block == null)
                {
                    block = this.blockAccess.getBlock(x, y, z);
                    this.blocks[index] = block;
                }

                return block;
            }
            else
            {
                return this.blockAccess.getBlock(x, y, z);
            }
        }
    }

    private int getPositionIndex(int x, int y, int z)
    {
        int i = x - this.position.getX();
        int j = y - this.position.getY();
        int k = z - this.position.getZ();
        return i >= 0 && j >= 0 && k >= 0 && i < 20 && j < 20 && k < 20 ? i * 400 + k * 20 + j : -1;
    }

    public void renderStart()
    {
        if (this.combinedLights == null)
        {
            this.combinedLights = (int[])((int[])cacheCombinedLights.allocate(8000));
        }

        Arrays.fill(this.combinedLights, -1);

        if (this.blocks == null)
        {
            this.blocks = (Block[])((Block[])cacheBlocks.allocate(8000));
        }

        Arrays.fill(this.blocks, (Object)null);
    }

    public void renderFinish()
    {
        cacheCombinedLights.free(this.combinedLights);
        this.combinedLights = null;
        cacheBlocks.free(this.blocks);
        this.blocks = null;
    }
}
