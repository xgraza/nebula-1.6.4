package net.minecraft.world.biome;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

public class BiomeCache
{
    private final WorldChunkManager chunkManager;
    private long lastCleanupTime;
    private LongHashMap cacheMap = new LongHashMap();
    private List cache = new ArrayList();
    private static final String __OBFID = "CL_00000162";

    public BiomeCache(WorldChunkManager par1WorldChunkManager)
    {
        this.chunkManager = par1WorldChunkManager;
    }

    public BiomeCache.Block getBiomeCacheBlock(int par1, int par2)
    {
        par1 >>= 4;
        par2 >>= 4;
        long var3 = (long)par1 & 4294967295L | ((long)par2 & 4294967295L) << 32;
        BiomeCache.Block var5 = (BiomeCache.Block)this.cacheMap.getValueByKey(var3);

        if (var5 == null)
        {
            var5 = new BiomeCache.Block(par1, par2);
            this.cacheMap.add(var3, var5);
            this.cache.add(var5);
        }

        var5.lastAccessTime = MinecraftServer.getSystemTimeMillis();
        return var5;
    }

    public BiomeGenBase getBiomeGenAt(int par1, int par2)
    {
        return this.getBiomeCacheBlock(par1, par2).getBiomeGenAt(par1, par2);
    }

    public void cleanupCache()
    {
        long var1 = MinecraftServer.getSystemTimeMillis();
        long var3 = var1 - this.lastCleanupTime;

        if (var3 > 7500L || var3 < 0L)
        {
            this.lastCleanupTime = var1;

            for (int var5 = 0; var5 < this.cache.size(); ++var5)
            {
                BiomeCache.Block var6 = (BiomeCache.Block)this.cache.get(var5);
                long var7 = var1 - var6.lastAccessTime;

                if (var7 > 30000L || var7 < 0L)
                {
                    this.cache.remove(var5--);
                    long var9 = (long)var6.xPosition & 4294967295L | ((long)var6.zPosition & 4294967295L) << 32;
                    this.cacheMap.remove(var9);
                }
            }
        }
    }

    public BiomeGenBase[] getCachedBiomes(int par1, int par2)
    {
        return this.getBiomeCacheBlock(par1, par2).biomes;
    }

    public class Block
    {
        public float[] rainfallValues = new float[256];
        public BiomeGenBase[] biomes = new BiomeGenBase[256];
        public int xPosition;
        public int zPosition;
        public long lastAccessTime;
        private static final String __OBFID = "CL_00000163";

        public Block(int par2, int par3)
        {
            this.xPosition = par2;
            this.zPosition = par3;
            BiomeCache.this.chunkManager.getRainfall(this.rainfallValues, par2 << 4, par3 << 4, 16, 16);
            BiomeCache.this.chunkManager.getBiomeGenAt(this.biomes, par2 << 4, par3 << 4, 16, 16, false);
        }

        public BiomeGenBase getBiomeGenAt(int par1, int par2)
        {
            return this.biomes[par1 & 15 | (par2 & 15) << 4];
        }
    }
}