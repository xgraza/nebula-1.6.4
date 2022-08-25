package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBlock extends Item
{
    public final Block block;
    private IIcon field_150938_b;
    private static final String __OBFID = "CL_00001772";

    public ItemBlock(Block p_i45328_1_)
    {
        this.block = p_i45328_1_;
    }

    public ItemBlock setUnlocalizedName(String p_150937_1_)
    {
        super.setUnlocalizedName(p_150937_1_);
        return this;
    }

    public int getSpriteNumber()
    {
        return this.block.getItemIconName() != null ? 1 : 0;
    }

    public IIcon getIconFromDamage(int par1)
    {
        return this.field_150938_b != null ? this.field_150938_b : this.block.getBlockTextureFromSide(1);
    }

    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        Block var11 = par3World.getBlock(par4, par5, par6);

        if (var11 == Blocks.snow_layer && (par3World.getBlockMetadata(par4, par5, par6) & 7) < 1)
        {
            par7 = 1;
        }
        else if (var11 != Blocks.vine && var11 != Blocks.tallgrass && var11 != Blocks.deadbush)
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
        }
        else if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
        {
            return false;
        }
        else if (par5 == 255 && this.block.getMaterial().isSolid())
        {
            return false;
        }
        else if (par3World.canPlaceEntityOnSide(this.block, par4, par5, par6, false, par7, par2EntityPlayer, par1ItemStack))
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

                par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), this.block.stepSound.func_150496_b(), (this.block.stepSound.func_150497_c() + 1.0F) / 2.0F, this.block.stepSound.func_150494_d() * 0.8F);
                --par1ItemStack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean func_150936_a(World p_150936_1_, int p_150936_2_, int p_150936_3_, int p_150936_4_, int p_150936_5_, EntityPlayer p_150936_6_, ItemStack p_150936_7_)
    {
        Block var8 = p_150936_1_.getBlock(p_150936_2_, p_150936_3_, p_150936_4_);

        if (var8 == Blocks.snow_layer)
        {
            p_150936_5_ = 1;
        }
        else if (var8 != Blocks.vine && var8 != Blocks.tallgrass && var8 != Blocks.deadbush)
        {
            if (p_150936_5_ == 0)
            {
                --p_150936_3_;
            }

            if (p_150936_5_ == 1)
            {
                ++p_150936_3_;
            }

            if (p_150936_5_ == 2)
            {
                --p_150936_4_;
            }

            if (p_150936_5_ == 3)
            {
                ++p_150936_4_;
            }

            if (p_150936_5_ == 4)
            {
                --p_150936_2_;
            }

            if (p_150936_5_ == 5)
            {
                ++p_150936_2_;
            }
        }

        return p_150936_1_.canPlaceEntityOnSide(this.block, p_150936_2_, p_150936_3_, p_150936_4_, false, p_150936_5_, (Entity)null, p_150936_7_);
    }

    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return this.block.getUnlocalizedName();
    }

    public String getUnlocalizedName()
    {
        return this.block.getUnlocalizedName();
    }

    public CreativeTabs getCreativeTab()
    {
        return this.block.getCreativeTabToDisplayOn();
    }

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
}
