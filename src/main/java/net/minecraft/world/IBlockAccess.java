package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.biome.BiomeGenBase;

public interface IBlockAccess
{
    Block getBlock(int var1, int var2, int var3);

    TileEntity getTileEntity(int var1, int var2, int var3);

    int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4);

    int getBlockMetadata(int var1, int var2, int var3);

    boolean isAirBlock(int var1, int var2, int var3);

    BiomeGenBase getBiomeGenForCoords(int var1, int var2);

    int getHeight();

    boolean extendedLevelsInChunkCache();

    Vec3Pool getWorldVec3Pool();

    int isBlockProvidingPowerTo(int var1, int var2, int var3, int var4);
}
