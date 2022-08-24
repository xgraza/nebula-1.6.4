package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenTaiga2 extends WorldGenAbstractTree
{
    private static final String __OBFID = "CL_00000435";

    public WorldGenTaiga2(boolean par1)
    {
        super(par1);
    }

    public boolean generate(World par1World, Random par2Random, int par3, int par4, int par5)
    {
        int var6 = par2Random.nextInt(4) + 6;
        int var7 = 1 + par2Random.nextInt(2);
        int var8 = var6 - var7;
        int var9 = 2 + par2Random.nextInt(2);
        boolean var10 = true;

        if (par4 >= 1 && par4 + var6 + 1 <= 256)
        {
            int var13;
            int var22;

            for (int var11 = par4; var11 <= par4 + 1 + var6 && var10; ++var11)
            {
                boolean var12 = true;

                if (var11 - par4 < var7)
                {
                    var22 = 0;
                }
                else
                {
                    var22 = var9;
                }

                for (var13 = par3 - var22; var13 <= par3 + var22 && var10; ++var13)
                {
                    for (int var14 = par5 - var22; var14 <= par5 + var22 && var10; ++var14)
                    {
                        if (var11 >= 0 && var11 < 256)
                        {
                            Block var15 = par1World.getBlock(var13, var11, var14);

                            if (var15.getMaterial() != Material.air && var15.getMaterial() != Material.leaves)
                            {
                                var10 = false;
                            }
                        }
                        else
                        {
                            var10 = false;
                        }
                    }
                }
            }

            if (!var10)
            {
                return false;
            }
            else
            {
                Block var21 = par1World.getBlock(par3, par4 - 1, par5);

                if ((var21 == Blocks.grass || var21 == Blocks.dirt || var21 == Blocks.farmland) && par4 < 256 - var6 - 1)
                {
                    this.func_150515_a(par1World, par3, par4 - 1, par5, Blocks.dirt);
                    var22 = par2Random.nextInt(2);
                    var13 = 1;
                    byte var23 = 0;
                    int var16;
                    int var24;

                    for (var24 = 0; var24 <= var8; ++var24)
                    {
                        var16 = par4 + var6 - var24;

                        for (int var17 = par3 - var22; var17 <= par3 + var22; ++var17)
                        {
                            int var18 = var17 - par3;

                            for (int var19 = par5 - var22; var19 <= par5 + var22; ++var19)
                            {
                                int var20 = var19 - par5;

                                if ((Math.abs(var18) != var22 || Math.abs(var20) != var22 || var22 <= 0) && !par1World.getBlock(var17, var16, var19).func_149730_j())
                                {
                                    this.func_150516_a(par1World, var17, var16, var19, Blocks.leaves, 1);
                                }
                            }
                        }

                        if (var22 >= var13)
                        {
                            var22 = var23;
                            var23 = 1;
                            ++var13;

                            if (var13 > var9)
                            {
                                var13 = var9;
                            }
                        }
                        else
                        {
                            ++var22;
                        }
                    }

                    var24 = par2Random.nextInt(3);

                    for (var16 = 0; var16 < var6 - var24; ++var16)
                    {
                        Block var25 = par1World.getBlock(par3, par4 + var16, par5);

                        if (var25.getMaterial() == Material.air || var25.getMaterial() == Material.leaves)
                        {
                            this.func_150516_a(par1World, par3, par4 + var16, par5, Blocks.log, 1);
                        }
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }
}
