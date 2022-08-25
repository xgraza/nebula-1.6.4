package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.util.IIcon;

public class ItemCloth extends ItemBlock
{
    private static final String __OBFID = "CL_00000075";

    public ItemCloth(Block p_i45358_1_)
    {
        super(p_i45358_1_);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public IIcon getIconFromDamage(int par1)
    {
        return this.block.func_149735_b(2, BlockColored.func_150032_b(par1));
    }

    public int getMetadata(int par1)
    {
        return par1;
    }

    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return super.getUnlocalizedName() + "." + ItemDye.field_150923_a[BlockColored.func_150032_b(par1ItemStack.getItemDamage())];
    }
}
