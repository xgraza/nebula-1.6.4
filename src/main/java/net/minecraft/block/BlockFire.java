package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

import java.util.Random;

public class BlockFire extends Block
{
    private final int[] field_149849_a = new int[256];
    private final int[] flammability = new int[256];
    private IIcon[] field_149850_M;

    protected BlockFire()
    {
        super(Material.fire);
        this.setTickRandomly(true);
    }

    public static void func_149843_e()
    {
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.planks), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.double_wooden_slab), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.wooden_slab), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.fence), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.oak_stairs), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.birch_stairs), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.spruce_stairs), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.jungle_stairs), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.log), 5, 5);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.log2), 5, 5);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.leaves), 30, 60);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.leaves2), 30, 60);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.bookshelf), 30, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.tnt), 15, 100);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.tallgrass), 60, 100);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.double_plant), 60, 100);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.yellow_flower), 60, 100);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.red_flower), 60, 100);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.wool), 30, 60);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.vine), 15, 100);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.coal_block), 5, 5);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.hay_block), 60, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.carpet), 60, 20);
    }

    public void func_149842_a(int blockID, int p_149842_2_, int flammability)
    {
        this.field_149849_a[blockID] = p_149842_2_;
        this.flammability[blockID] = flammability;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 3;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random p_149745_1_)
    {
        return 0;
    }

    public int tickRate(World p_149738_1_)
    {
        return 30;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        if (world.getGameRules().getGameRuleBooleanValue("doFireTick"))
        {
            boolean continueBurn = world.getBlock(x, y - 1, z) == Blocks.netherrack;

            if (world.provider instanceof WorldProviderEnd && world.getBlock(x, y - 1, z) == Blocks.bedrock)
            {
                continueBurn = true;
            }

            if (!this.canPlaceBlockAt(world, x, y, z))
            {
                world.setBlockToAir(x, y, z);
            }

            if (!continueBurn && world.isRaining() && (world.canLightningStrikeAt(x, y, z) || world.canLightningStrikeAt(x - 1, y, z) || world.canLightningStrikeAt(x + 1, y, z) || world.canLightningStrikeAt(x, y, z - 1) || world.canLightningStrikeAt(x, y, z + 1)))
            {
                world.setBlockToAir(x, y, z);
            } else
            {
                int meta = world.getBlockMetadata(x, y, z);

                if (meta < 15)
                {
                    world.setBlockMetadataWithNotify(x, y, z, meta + rand.nextInt(3) / 2, 4);
                }

                world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world) + rand.nextInt(10));

                if (!continueBurn && !this.func_149847_e(world, x, y, z))
                {
                    if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z) || meta > 3)
                    {
                        world.setBlockToAir(x, y, z);
                    }
                } else if (!continueBurn && !this.canBlockCatchFire(world, x, y - 1, z) && meta == 15 && rand.nextInt(4) == 0)
                {
                    world.setBlockToAir(x, y, z);
                } else
                {
                    boolean highHumidity = world.isBlockHighHumidity(x, y, z);
                    byte humidity = (byte) (highHumidity ? -50 : 0);

                    for (EnumFacing facing : EnumFacing.values())
                    {
                        int baseHumidity = facing.getFrontOffsetY() != 0 ? 250 : 300;
                        catchFire(world,
                                x + facing.getFrontOffsetX(),
                                y + facing.getFrontOffsetY(),
                                z + facing.getFrontOffsetZ(),
                                baseHumidity + humidity, rand, meta);
                    }
//                    this.func_149841_a(world, x + 1, y, z, 300 + humidity, rand, var7);
//                    this.func_149841_a(world, x - 1, y, z, 300 + humidity, rand, var7);
//                    this.func_149841_a(world, x, y - 1, z, 250 + humidity, rand, var7);
//                    this.func_149841_a(world, x, y + 1, z, 250 + humidity, rand, var7);
//                    this.func_149841_a(world, x, y, z - 1, 300 + humidity, rand, var7);
//                    this.func_149841_a(world, x, y, z + 1, 300 + humidity, rand, var7);

                    for (int posX = x - 1; posX <= x + 1; ++posX)
                    {
                        for (int posY = z - 1; posY <= z + 1; ++posY)
                        {
                            for (int posZ = y - 1; posZ <= y + 4; ++posZ)
                            {
                                if (posX != x || posZ != y || posY != z)
                                {
                                    int var13 = 100;

                                    if (posZ > y + 1)
                                    {
                                        var13 += (posZ - (y + 1)) * 100;
                                    }

                                    int var14 = this.func_149845_m(world, posX, posZ, posY);

                                    if (var14 > 0)
                                    {
                                        int var15 = (var14 + 40 + world.difficultySetting.getDifficultyId() * 7) / (meta + 30);

                                        if (highHumidity)
                                        {
                                            var15 /= 2;
                                        }

                                        if (var15 > 0 && rand.nextInt(var13) <= var15 && (!world.isRaining() || !world.canLightningStrikeAt(posX, posZ, posY)) && !world.canLightningStrikeAt(posX - 1, posZ, z) && !world.canLightningStrikeAt(posX + 1, posZ, posY) && !world.canLightningStrikeAt(posX, posZ, posY - 1) && !world.canLightningStrikeAt(posX, posZ, posY + 1))
                                        {
                                            int var16 = meta + rand.nextInt(5) / 4;

                                            if (var16 > 15)
                                            {
                                                var16 = 15;
                                            }

                                            world.setBlock(posX, posZ, posY, this, var16, 3);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean func_149698_L()
    {
        return false;
    }

    private void catchFire(World world, int x, int y, int z, int humidity, Random rand, int meta)
    {
        int var8 = this.flammability[Block.getIdFromBlock(world.getBlock(x, y, z))];

        if (rand.nextInt(humidity) < var8)
        {
            boolean isTNT = world.getBlock(x, y, z) == Blocks.tnt;

            if (rand.nextInt(meta + 10) < 5 && !world.canLightningStrikeAt(x, y, z))
            {
                int var10 = meta + rand.nextInt(5) / 4;

                if (var10 > 15)
                {
                    var10 = 15;
                }

                world.setBlock(x, y, z, this, var10, 3);
            } else
            {
                world.setBlockToAir(x, y, z);
            }

            if (isTNT)
            {
                Blocks.tnt.onBlockDestroyedByPlayer(world, x, y, z, 1);
            }
        }
    }

    private boolean func_149847_e(World p_149847_1_, int p_149847_2_, int p_149847_3_, int p_149847_4_)
    {
        return this.canBlockCatchFire(p_149847_1_, p_149847_2_ + 1, p_149847_3_, p_149847_4_) || (this.canBlockCatchFire(p_149847_1_, p_149847_2_ - 1, p_149847_3_, p_149847_4_) || (this.canBlockCatchFire(p_149847_1_, p_149847_2_, p_149847_3_ - 1, p_149847_4_) || (this.canBlockCatchFire(p_149847_1_, p_149847_2_, p_149847_3_ + 1, p_149847_4_) || (this.canBlockCatchFire(p_149847_1_, p_149847_2_, p_149847_3_, p_149847_4_ - 1) || this.canBlockCatchFire(p_149847_1_, p_149847_2_, p_149847_3_, p_149847_4_ + 1)))));
    }

    private int func_149845_m(World world, int x, int y, int z)
    {
        if (!world.isAirBlock(x, y, z))
        {
            return 0;
        }

        int var6 = 0;
        for (EnumFacing facing : EnumFacing.values())
        {
            var6 = func_149846_a(world,
                    x + facing.getFrontOffsetX(),
                    y + facing.getFrontOffsetY(),
                    z + facing.getFrontOffsetZ(), var6);
        }

//        byte var5 = 0;
//        int var6 = this.func_149846_a(world, x + 1, y, z, var5);
//        var6 = this.func_149846_a(world, x - 1, y, z, var6);
//        var6 = this.func_149846_a(world, x, y - 1, z, var6);
//        var6 = this.func_149846_a(world, x, y + 1, z, var6);
//        var6 = this.func_149846_a(world, x, y, z - 1, var6);
//        var6 = this.func_149846_a(world, x, y, z + 1, var6);
        return var6;
    }

    public boolean isCollidable()
    {
        return false;
    }

    public boolean canBlockCatchFire(IBlockAccess p_149844_1_, int p_149844_2_, int p_149844_3_, int p_149844_4_)
    {
        return this.field_149849_a[Block.getIdFromBlock(p_149844_1_.getBlock(p_149844_2_, p_149844_3_, p_149844_4_))] > 0;
    }

    public int func_149846_a(World p_149846_1_, int p_149846_2_, int p_149846_3_, int p_149846_4_, int p_149846_5_)
    {
        int var6 = this.field_149849_a[Block.getIdFromBlock(p_149846_1_.getBlock(p_149846_2_, p_149846_3_, p_149846_4_))];
        return var6 > p_149846_5_ ? var6 : p_149846_5_;
    }

    public boolean canPlaceBlockAt(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
    {
        return World.doesBlockHaveSolidTopSurface(p_149742_1_, p_149742_2_, p_149742_3_ - 1, p_149742_4_) || this.func_149847_e(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_);
    }

    public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        if (!World.doesBlockHaveSolidTopSurface(p_149695_1_, p_149695_2_, p_149695_3_ - 1, p_149695_4_) && !this.func_149847_e(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_))
        {
            p_149695_1_.setBlockToAir(p_149695_2_, p_149695_3_, p_149695_4_);
        }
    }

    public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        if (p_149726_1_.provider.dimensionId > 0 || !Blocks.portal.func_150000_e(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_))
        {
            if (!World.doesBlockHaveSolidTopSurface(p_149726_1_, p_149726_2_, p_149726_3_ - 1, p_149726_4_) && !this.func_149847_e(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_))
            {
                p_149726_1_.setBlockToAir(p_149726_2_, p_149726_3_, p_149726_4_);
            } else
            {
                p_149726_1_.scheduleBlockUpdate(p_149726_2_, p_149726_3_, p_149726_4_, this, this.tickRate(p_149726_1_) + p_149726_1_.rand.nextInt(10));
            }
        }
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_)
    {
        if (p_149734_5_.nextInt(24) == 0)
        {
            p_149734_1_.playSound((float) p_149734_2_ + 0.5F, (float) p_149734_3_ + 0.5F, (float) p_149734_4_ + 0.5F, "fire.fire", 1.0F + p_149734_5_.nextFloat(), p_149734_5_.nextFloat() * 0.7F + 0.3F, false);
        }

        int var6;
        float var7;
        float var8;
        float var9;

        if (!World.doesBlockHaveSolidTopSurface(p_149734_1_, p_149734_2_, p_149734_3_ - 1, p_149734_4_) && !Blocks.fire.canBlockCatchFire(p_149734_1_, p_149734_2_, p_149734_3_ - 1, p_149734_4_))
        {
            if (Blocks.fire.canBlockCatchFire(p_149734_1_, p_149734_2_ - 1, p_149734_3_, p_149734_4_))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float) p_149734_2_ + p_149734_5_.nextFloat() * 0.1F;
                    var8 = (float) p_149734_3_ + p_149734_5_.nextFloat();
                    var9 = (float) p_149734_4_ + p_149734_5_.nextFloat();
                    p_149734_1_.spawnParticle("largesmoke", var7, var8, var9, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.fire.canBlockCatchFire(p_149734_1_, p_149734_2_ + 1, p_149734_3_, p_149734_4_))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float) (p_149734_2_ + 1) - p_149734_5_.nextFloat() * 0.1F;
                    var8 = (float) p_149734_3_ + p_149734_5_.nextFloat();
                    var9 = (float) p_149734_4_ + p_149734_5_.nextFloat();
                    p_149734_1_.spawnParticle("largesmoke", var7, var8, var9, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.fire.canBlockCatchFire(p_149734_1_, p_149734_2_, p_149734_3_, p_149734_4_ - 1))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float) p_149734_2_ + p_149734_5_.nextFloat();
                    var8 = (float) p_149734_3_ + p_149734_5_.nextFloat();
                    var9 = (float) p_149734_4_ + p_149734_5_.nextFloat() * 0.1F;
                    p_149734_1_.spawnParticle("largesmoke", var7, var8, var9, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.fire.canBlockCatchFire(p_149734_1_, p_149734_2_, p_149734_3_, p_149734_4_ + 1))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float) p_149734_2_ + p_149734_5_.nextFloat();
                    var8 = (float) p_149734_3_ + p_149734_5_.nextFloat();
                    var9 = (float) (p_149734_4_ + 1) - p_149734_5_.nextFloat() * 0.1F;
                    p_149734_1_.spawnParticle("largesmoke", var7, var8, var9, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.fire.canBlockCatchFire(p_149734_1_, p_149734_2_, p_149734_3_ + 1, p_149734_4_))
            {
                for (var6 = 0; var6 < 2; ++var6)
                {
                    var7 = (float) p_149734_2_ + p_149734_5_.nextFloat();
                    var8 = (float) (p_149734_3_ + 1) - p_149734_5_.nextFloat() * 0.1F;
                    var9 = (float) p_149734_4_ + p_149734_5_.nextFloat();
                    p_149734_1_.spawnParticle("largesmoke", var7, var8, var9, 0.0D, 0.0D, 0.0D);
                }
            }
        } else
        {
            for (var6 = 0; var6 < 3; ++var6)
            {
                var7 = (float) p_149734_2_ + p_149734_5_.nextFloat();
                var8 = (float) p_149734_3_ + p_149734_5_.nextFloat() * 0.5F + 0.5F;
                var9 = (float) p_149734_4_ + p_149734_5_.nextFloat();
                p_149734_1_.spawnParticle("largesmoke", var7, var8, var9, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void registerIcons(IIconRegister p_149651_1_)
    {
        this.field_149850_M = new IIcon[]{ p_149651_1_.registerIcon(this.getTextureName() + "_layer_0"), p_149651_1_.registerIcon(this.getTextureName() + "_layer_1") };
    }

    public IIcon getFireIcon(int p_149840_1_)
    {
        return this.field_149850_M[p_149840_1_];
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return this.field_149850_M[0];
    }

    public MapColor getMapColor(int p_149728_1_)
    {
        return MapColor.field_151656_f;
    }
}
