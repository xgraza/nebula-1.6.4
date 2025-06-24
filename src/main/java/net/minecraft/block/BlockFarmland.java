package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockFarmland extends Block
{
    private IIcon wetIcon, dryIcon;

    protected BlockFarmland()
    {
        super(Material.ground);
        this.setTickRandomly(true);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
        this.setLightOpacity(255);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return AxisAlignedBB.getAABBPool().getAABB(p_149668_2_, p_149668_3_, p_149668_4_, p_149668_2_ + 1, p_149668_3_ + 1, p_149668_4_ + 1);
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
     * Gets the block's texture. Args: side, meta
     */
    public IIcon getIcon(int side, int meta)
    {
        return side == 1 ? (meta > 0 ? this.wetIcon : this.dryIcon) : Blocks.dirt.getBlockTextureFromSide(side);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World theWorld, int x, int y, int z, Random rng)
    {
        if (!this.isNearWater(theWorld, x, y, z) && !theWorld.canLightningStrikeAt(x, y + 1, z))
        {
            final int meta = theWorld.getBlockMetadata(x, y, z);
            if (meta > 0)
            {
                theWorld.setBlockMetadataWithNotify(x, y, z, meta - 1, 2);
            }
            else if (!this.hasPlant(theWorld, x, y, z))
            {
                theWorld.setBlock(x, y, z, Blocks.dirt);
            }
        }
        else
        {
            theWorld.setBlockMetadataWithNotify(x, y, z, 7, 2);
        }
    }

    /**
     * Block's chance to react to an entity falling on it.
     */
    public void onFallenUpon(World theWorld, int x, int y, int z, Entity entity, float fallDistance)
    {
        if (!theWorld.isClient && theWorld.rand.nextFloat() < fallDistance - 0.5F)
        {
            if (!(entity instanceof EntityPlayer) && !theWorld.getGameRules().getGameRuleBooleanValue("mobGriefing"))
            {
                return;
            }

            theWorld.setBlock(x, y, z, Blocks.dirt);
        }
    }

    private boolean hasPlant(World theWorld, int x, int y, int z)
    {
        byte var5 = 0;

        for (int var6 = x - var5; var6 <= x + var5; ++var6)
        {
            for (int var7 = z - var5; var7 <= z + var5; ++var7)
            {
                Block block = theWorld.getBlock(var6, y + 1, var7);
                if (block == Blocks.wheat || block == Blocks.melon_stem || block == Blocks.pumpkin_stem || block == Blocks.potatoes || block == Blocks.carrots)
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isNearWater(World p_149821_1_, int p_149821_2_, int p_149821_3_, int p_149821_4_)
    {
        for (int var5 = p_149821_2_ - 4; var5 <= p_149821_2_ + 4; ++var5)
        {
            for (int var6 = p_149821_3_; var6 <= p_149821_3_ + 1; ++var6)
            {
                for (int var7 = p_149821_4_ - 4; var7 <= p_149821_4_ + 4; ++var7)
                {
                    if (p_149821_1_.getBlock(var5, var6, var7).getMaterial() == Material.water)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        super.onNeighborBlockChange(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_5_);
        Material var6 = p_149695_1_.getBlock(p_149695_2_, p_149695_3_ + 1, p_149695_4_).getMaterial();

        if (var6.isSolid())
        {
            p_149695_1_.setBlock(p_149695_2_, p_149695_3_, p_149695_4_, Blocks.dirt);
        }
    }

    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Blocks.dirt.getItemDropped(0, p_149650_2_, p_149650_3_);
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    public Item getItemPicked(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Item.getItemFromBlock(Blocks.dirt);
    }

    public void registerIcons(IIconRegister registry)
    {
        this.wetIcon = registry.registerIcon(this.getTextureName() + "_wet");
        this.dryIcon = registry.registerIcon(this.getTextureName() + "_dry");
    }
}
