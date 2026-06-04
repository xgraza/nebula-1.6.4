package net.minecraft.block;

import net.minecraft.world.World;

import java.util.Random;

public interface IGrowable
{
    boolean canGrow(World var1, int var2, int var3, int var4, boolean var5);

    boolean canGrowRandom(World var1, Random var2, int var3, int var4, int var5);

    void grow(World var1, Random var2, int var3, int var4, int var5);
}
