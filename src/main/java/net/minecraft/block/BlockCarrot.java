package net.minecraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

public class BlockCarrot extends BlockCrops
{
    private IIcon[] field_149868_a;
    private static final String __OBFID = "CL_00000212";

    /**
     * Gets the block's texture. Args: side, meta
     */
    public IIcon getIcon(int side, int meta)
    {
        if (meta < 7)
        {
            if (meta == 6)
            {
                meta = 5;
            }

            return this.field_149868_a[meta >> 1];
        }
        else
        {
            return this.field_149868_a[3];
        }
    }

    protected Item func_149866_i()
    {
        return Items.carrot;
    }

    protected Item func_149865_P()
    {
        return Items.carrot;
    }

    public void registerIcons(IIconRegister p_149651_1_)
    {
        this.field_149868_a = new IIcon[4];

        for (int var2 = 0; var2 < this.field_149868_a.length; ++var2)
        {
            this.field_149868_a[var2] = p_149651_1_.registerIcon(this.getTextureName() + "_stage_" + var2);
        }
    }
}
