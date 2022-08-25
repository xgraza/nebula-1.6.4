package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenWaterlily;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeDecorator
{
    protected World currentWorld;
    protected Random randomGenerator;
    protected int chunk_X;
    protected int chunk_Z;
    protected WorldGenerator clayGen = new WorldGenClay(4);
    protected WorldGenerator sandGen;
    protected WorldGenerator gravelAsSandGen;
    protected WorldGenerator dirtGen;
    protected WorldGenerator gravelGen;
    protected WorldGenerator coalGen;
    protected WorldGenerator ironGen;
    protected WorldGenerator goldGen;
    protected WorldGenerator redstoneGen;
    protected WorldGenerator diamondGen;
    protected WorldGenerator lapisGen;
    protected WorldGenFlowers field_150514_p;
    protected WorldGenerator mushroomBrownGen;
    protected WorldGenerator mushroomRedGen;
    protected WorldGenerator bigMushroomGen;
    protected WorldGenerator reedGen;
    protected WorldGenerator cactusGen;
    protected WorldGenerator waterlilyGen;
    protected int waterlilyPerChunk;
    protected int treesPerChunk;
    protected int flowersPerChunk;
    protected int grassPerChunk;
    protected int deadBushPerChunk;
    protected int mushroomsPerChunk;
    protected int reedsPerChunk;
    protected int cactiPerChunk;
    protected int sandPerChunk;
    protected int sandPerChunk2;
    protected int clayPerChunk;
    protected int bigMushroomsPerChunk;
    public boolean generateLakes;
    private static final String __OBFID = "CL_00000164";

    public BiomeDecorator()
    {
        this.sandGen = new WorldGenSand(Blocks.sand, 7);
        this.gravelAsSandGen = new WorldGenSand(Blocks.gravel, 6);
        this.dirtGen = new WorldGenMinable(Blocks.dirt, 32);
        this.gravelGen = new WorldGenMinable(Blocks.gravel, 32);
        this.coalGen = new WorldGenMinable(Blocks.coal_ore, 16);
        this.ironGen = new WorldGenMinable(Blocks.iron_ore, 8);
        this.goldGen = new WorldGenMinable(Blocks.gold_ore, 8);
        this.redstoneGen = new WorldGenMinable(Blocks.redstone_ore, 7);
        this.diamondGen = new WorldGenMinable(Blocks.diamond_ore, 7);
        this.lapisGen = new WorldGenMinable(Blocks.lapis_ore, 6);
        this.field_150514_p = new WorldGenFlowers(Blocks.yellow_flower);
        this.mushroomBrownGen = new WorldGenFlowers(Blocks.brown_mushroom);
        this.mushroomRedGen = new WorldGenFlowers(Blocks.red_mushroom);
        this.bigMushroomGen = new WorldGenBigMushroom();
        this.reedGen = new WorldGenReed();
        this.cactusGen = new WorldGenCactus();
        this.waterlilyGen = new WorldGenWaterlily();
        this.flowersPerChunk = 2;
        this.grassPerChunk = 1;
        this.sandPerChunk = 1;
        this.sandPerChunk2 = 3;
        this.clayPerChunk = 1;
        this.generateLakes = true;
    }

    public void func_150512_a(World p_150512_1_, Random p_150512_2_, BiomeGenBase p_150512_3_, int p_150512_4_, int p_150512_5_)
    {
        if (this.currentWorld != null)
        {
            throw new RuntimeException("Already decorating!!");
        }
        else
        {
            this.currentWorld = p_150512_1_;
            this.randomGenerator = p_150512_2_;
            this.chunk_X = p_150512_4_;
            this.chunk_Z = p_150512_5_;
            this.func_150513_a(p_150512_3_);
            this.currentWorld = null;
            this.randomGenerator = null;
        }
    }

    protected void func_150513_a(BiomeGenBase p_150513_1_)
    {
        this.generateOres();
        int var2;
        int var3;
        int var4;

        for (var2 = 0; var2 < this.sandPerChunk2; ++var2)
        {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.sandGen.generate(this.currentWorld, this.randomGenerator, var3, this.currentWorld.getTopSolidOrLiquidBlock(var3, var4), var4);
        }

        for (var2 = 0; var2 < this.clayPerChunk; ++var2)
        {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.clayGen.generate(this.currentWorld, this.randomGenerator, var3, this.currentWorld.getTopSolidOrLiquidBlock(var3, var4), var4);
        }

        for (var2 = 0; var2 < this.sandPerChunk; ++var2)
        {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.gravelAsSandGen.generate(this.currentWorld, this.randomGenerator, var3, this.currentWorld.getTopSolidOrLiquidBlock(var3, var4), var4);
        }

        var2 = this.treesPerChunk;

        if (this.randomGenerator.nextInt(10) == 0)
        {
            ++var2;
        }

        int var5;
        int var6;

        for (var3 = 0; var3 < var2; ++var3)
        {
            var4 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var5 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var6 = this.currentWorld.getHeightValue(var4, var5);
            WorldGenAbstractTree var7 = p_150513_1_.func_150567_a(this.randomGenerator);
            var7.setScale(1.0D, 1.0D, 1.0D);

            if (var7.generate(this.currentWorld, this.randomGenerator, var4, var6, var5))
            {
                var7.func_150524_b(this.currentWorld, this.randomGenerator, var4, var6, var5);
            }
        }

        for (var3 = 0; var3 < this.bigMushroomsPerChunk; ++var3)
        {
            var4 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var5 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.bigMushroomGen.generate(this.currentWorld, this.randomGenerator, var4, this.currentWorld.getHeightValue(var4, var5), var5);
        }

        for (var3 = 0; var3 < this.flowersPerChunk; ++var3)
        {
            var4 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var5 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var6 = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(var4, var5) + 32);
            String var9 = p_150513_1_.func_150572_a(this.randomGenerator, var4, var6, var5);
            BlockFlower var8 = BlockFlower.func_149857_e(var9);

            if (var8.getMaterial() != Material.air)
            {
                this.field_150514_p.func_150550_a(var8, BlockFlower.func_149856_f(var9));
                this.field_150514_p.generate(this.currentWorld, this.randomGenerator, var4, var6, var5);
            }
        }

        for (var3 = 0; var3 < this.grassPerChunk; ++var3)
        {
            var4 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var5 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var6 = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(var4, var5) * 2);
            WorldGenerator var10 = p_150513_1_.getRandomWorldGenForGrass(this.randomGenerator);
            var10.generate(this.currentWorld, this.randomGenerator, var4, var6, var5);
        }

        for (var3 = 0; var3 < this.deadBushPerChunk; ++var3)
        {
            var4 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var5 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var6 = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(var4, var5) * 2);
            (new WorldGenDeadBush(Blocks.deadbush)).generate(this.currentWorld, this.randomGenerator, var4, var6, var5);
        }

        for (var3 = 0; var3 < this.waterlilyPerChunk; ++var3)
        {
            var4 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var5 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;

            for (var6 = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(var4, var5) * 2); var6 > 0 && this.currentWorld.isAirBlock(var4, var6 - 1, var5); --var6)
            {
                ;
            }

            this.waterlilyGen.generate(this.currentWorld, this.randomGenerator, var4, var6, var5);
        }

        for (var3 = 0; var3 < this.mushroomsPerChunk; ++var3)
        {
            if (this.randomGenerator.nextInt(4) == 0)
            {
                var4 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                var5 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                var6 = this.currentWorld.getHeightValue(var4, var5);
                this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, var4, var6, var5);
            }

            if (this.randomGenerator.nextInt(8) == 0)
            {
                var4 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                var5 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                var6 = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(var4, var5) * 2);
                this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, var4, var6, var5);
            }
        }

        if (this.randomGenerator.nextInt(4) == 0)
        {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var5 = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(var3, var4) * 2);
            this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, var3, var5, var4);
        }

        if (this.randomGenerator.nextInt(8) == 0)
        {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var5 = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(var3, var4) * 2);
            this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, var3, var5, var4);
        }

        for (var3 = 0; var3 < this.reedsPerChunk; ++var3)
        {
            var4 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var5 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var6 = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(var4, var5) * 2);
            this.reedGen.generate(this.currentWorld, this.randomGenerator, var4, var6, var5);
        }

        for (var3 = 0; var3 < 10; ++var3)
        {
            var4 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var5 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var6 = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(var4, var5) * 2);
            this.reedGen.generate(this.currentWorld, this.randomGenerator, var4, var6, var5);
        }

        if (this.randomGenerator.nextInt(32) == 0)
        {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var5 = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(var3, var4) * 2);
            (new WorldGenPumpkin()).generate(this.currentWorld, this.randomGenerator, var3, var5, var4);
        }

        for (var3 = 0; var3 < this.cactiPerChunk; ++var3)
        {
            var4 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var5 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var6 = this.randomGenerator.nextInt(this.currentWorld.getHeightValue(var4, var5) * 2);
            this.cactusGen.generate(this.currentWorld, this.randomGenerator, var4, var6, var5);
        }

        if (this.generateLakes)
        {
            for (var3 = 0; var3 < 50; ++var3)
            {
                var4 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                var5 = this.randomGenerator.nextInt(this.randomGenerator.nextInt(248) + 8);
                var6 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                (new WorldGenLiquids(Blocks.flowing_water)).generate(this.currentWorld, this.randomGenerator, var4, var5, var6);
            }

            for (var3 = 0; var3 < 20; ++var3)
            {
                var4 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                var5 = this.randomGenerator.nextInt(this.randomGenerator.nextInt(this.randomGenerator.nextInt(240) + 8) + 8);
                var6 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                (new WorldGenLiquids(Blocks.flowing_lava)).generate(this.currentWorld, this.randomGenerator, var4, var5, var6);
            }
        }
    }

    protected void genStandardOre1(int par1, WorldGenerator par2WorldGenerator, int par3, int par4)
    {
        for (int var5 = 0; var5 < par1; ++var5)
        {
            int var6 = this.chunk_X + this.randomGenerator.nextInt(16);
            int var7 = this.randomGenerator.nextInt(par4 - par3) + par3;
            int var8 = this.chunk_Z + this.randomGenerator.nextInt(16);
            par2WorldGenerator.generate(this.currentWorld, this.randomGenerator, var6, var7, var8);
        }
    }

    protected void genStandardOre2(int par1, WorldGenerator par2WorldGenerator, int par3, int par4)
    {
        for (int var5 = 0; var5 < par1; ++var5)
        {
            int var6 = this.chunk_X + this.randomGenerator.nextInt(16);
            int var7 = this.randomGenerator.nextInt(par4) + this.randomGenerator.nextInt(par4) + (par3 - par4);
            int var8 = this.chunk_Z + this.randomGenerator.nextInt(16);
            par2WorldGenerator.generate(this.currentWorld, this.randomGenerator, var6, var7, var8);
        }
    }

    protected void generateOres()
    {
        this.genStandardOre1(20, this.dirtGen, 0, 256);
        this.genStandardOre1(10, this.gravelGen, 0, 256);
        this.genStandardOre1(20, this.coalGen, 0, 128);
        this.genStandardOre1(20, this.ironGen, 0, 64);
        this.genStandardOre1(2, this.goldGen, 0, 32);
        this.genStandardOre1(8, this.redstoneGen, 0, 16);
        this.genStandardOre1(1, this.diamondGen, 0, 16);
        this.genStandardOre2(1, this.lapisGen, 16, 16);
    }
}
