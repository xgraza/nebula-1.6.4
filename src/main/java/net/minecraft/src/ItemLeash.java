package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class ItemLeash extends Item
{
    public ItemLeash(int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        int var11 = par3World.getBlockId(par4, par5, par6);

        if (Block.blocksList[var11] != null && Block.blocksList[var11].getRenderType() == 11)
        {
            if (par3World.isRemote)
            {
                return true;
            }
            else
            {
                func_135066_a(par2EntityPlayer, par3World, par4, par5, par6);
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public static boolean func_135066_a(EntityPlayer par0EntityPlayer, World par1World, int par2, int par3, int par4)
    {
        EntityLeashKnot var5 = EntityLeashKnot.getKnotForBlock(par1World, par2, par3, par4);
        boolean var6 = false;
        double var7 = 7.0D;
        List var9 = par1World.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB((double)par2 - var7, (double)par3 - var7, (double)par4 - var7, (double)par2 + var7, (double)par3 + var7, (double)par4 + var7));

        if (var9 != null)
        {
            Iterator var10 = var9.iterator();

            while (var10.hasNext())
            {
                EntityLiving var11 = (EntityLiving)var10.next();

                if (var11.getLeashed() && var11.getLeashedToEntity() == par0EntityPlayer)
                {
                    if (var5 == null)
                    {
                        var5 = EntityLeashKnot.func_110129_a(par1World, par2, par3, par4);
                    }

                    var11.setLeashedToEntity(var5, true);
                    var6 = true;
                }
            }
        }

        return var6;
    }
}
