package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public class ItemBlockWithMetadata extends ItemBlock
{
    private Block field_150950_b;
    private static final String __OBFID = "CL_00001769";

    public ItemBlockWithMetadata(Block p_i45326_1_, Block p_i45326_2_)
    {
        super(p_i45326_1_);
        this.field_150950_b = p_i45326_2_;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public IIcon getIconFromDamage(int par1)
    {
        return this.field_150950_b.getIcon(2, par1);
    }

    public int getMetadata(int par1)
    {
        return par1;
    }
}
