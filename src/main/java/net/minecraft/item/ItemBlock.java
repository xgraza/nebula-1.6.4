package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class ItemBlock extends Item
{
    protected final Block block;
    private IIcon field_150938_b;
    private static final String __OBFID = "CL_00001772";

    public ItemBlock(Block p_i45328_1_)
    {
        this.block = p_i45328_1_;
    }

    /**
     * Sets the unlocalized name of this item to the string passed as the parameter, prefixed by "item."
     */
    public ItemBlock setUnlocalizedName(String p_150937_1_)
    {
        super.setUnlocalizedName(p_150937_1_);
        return this;
    }

    /**
     * Returns 0 for /terrain.png, 1 for /gui/items.png
     */
    public int getSpriteNumber()
    {
        return this.block.getItemIconName() != null ? 1 : 0;
    }

    /**
     * Gets an icon index based on an item's damage value
     */
    public IIcon getIconFromDamage(int par1)
    {
        return this.field_150938_b != null ? this.field_150938_b : this.block.getBlockTextureFromSide(1);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        Block var11 = par3World.getBlock(par4, par5, par6);

        if (var11 == Blocks.snow_layer && (par3World.getBlockMetadata(par4, par5, par6) & 7) < 1)
        {
            par7 = 1;
        } else if (var11 != Blocks.vine && var11 != Blocks.tallgrass && var11 != Blocks.deadbush)
        {
            if (par7 == 0)
            {
                --par5;
            }

            if (par7 == 1)
            {
                ++par5;
            }

            if (par7 == 2)
            {
                --par6;
            }

            if (par7 == 3)
            {
                ++par6;
            }

            if (par7 == 4)
            {
                --par4;
            }

            if (par7 == 5)
            {
                ++par4;
            }
        }

        if (par1ItemStack.stackSize == 0)
        {
            return false;
        } else if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
        {
            return false;
        } else if (par5 == 255 && this.block.getMaterial().isSolid())
        {
            return false;
        } else if (par3World.canPlaceEntityOnSide(this.block, par4, par5, par6, false, par7, par2EntityPlayer, par1ItemStack))
        {
            int var12 = this.getMetadata(par1ItemStack.getItemDamage());
            int var13 = this.block.onBlockPlaced(par3World, par4, par5, par6, par7, par8, par9, par10, var12);

            if (par3World.setBlock(par4, par5, par6, this.block, var13, 3))
            {
                if (par3World.getBlock(par4, par5, par6) == this.block)
                {
                    this.block.onBlockPlacedBy(par3World, par4, par5, par6, par2EntityPlayer, par1ItemStack);
                    this.block.onPostBlockPlaced(par3World, par4, par5, par6, var13);
                }

                par3World.playSoundEffect((float) par4 + 0.5F, (float) par5 + 0.5F, (float) par6 + 0.5F, this.block.stepSound.func_150496_b(), (this.block.stepSound.func_150497_c() + 1.0F) / 2.0F, this.block.stepSound.func_150494_d() * 0.8F);
                --par1ItemStack.stackSize;
            }

            return true;
        } else
        {
            return false;
        }
    }

    public boolean canPlaceBlock(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack)
    {
        Block block = world.getBlock(x, y, z);

        if (block == Blocks.snow_layer)
        {
            side = 1;
        } else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush)
        {
            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }
        }

        return world.canPlaceEntityOnSide(block, x, y, z, false, side, null, stack);
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return this.block.getUnlocalizedName();
    }

    /**
     * Returns the unlocalized name of this item.
     */
    public String getUnlocalizedName()
    {
        return this.block.getUnlocalizedName();
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    public CreativeTabs getCreativeTab()
    {
        return this.block.getCreativeTabToDisplayOn();
    }

    /**
     * This returns the sub items
     */
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_)
    {
        this.block.getSubBlocks(p_150895_1_, p_150895_2_, p_150895_3_);
    }

    public void registerIcons(IIconRegister par1IconRegister)
    {
        String var2 = this.block.getItemIconName();

        if (var2 != null)
        {
            this.field_150938_b = par1IconRegister.registerIcon(var2);
        }
    }

    public Block getBlock()
    {
        return block;
    }
}
