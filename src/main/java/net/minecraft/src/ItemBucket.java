package net.minecraft.src;

public class ItemBucket extends Item
{
    /** field for checking if the bucket has been filled. */
    private int isFull;

    public ItemBucket(int par1, int par2)
    {
        super(par1);
        this.maxStackSize = 1;
        this.isFull = par2;
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        boolean var4 = this.isFull == 0;
        MovingObjectPosition var5 = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, var4);

        if (var5 == null)
        {
            return par1ItemStack;
        }
        else
        {
            if (var5.typeOfHit == EnumMovingObjectType.TILE)
            {
                int var6 = var5.blockX;
                int var7 = var5.blockY;
                int var8 = var5.blockZ;

                if (!par2World.canMineBlock(par3EntityPlayer, var6, var7, var8))
                {
                    return par1ItemStack;
                }

                if (this.isFull == 0)
                {
                    if (!par3EntityPlayer.canPlayerEdit(var6, var7, var8, var5.sideHit, par1ItemStack))
                    {
                        return par1ItemStack;
                    }

                    if (par2World.getBlockMaterial(var6, var7, var8) == Material.water && par2World.getBlockMetadata(var6, var7, var8) == 0)
                    {
                        par2World.setBlockToAir(var6, var7, var8);

                        if (par3EntityPlayer.capabilities.isCreativeMode)
                        {
                            return par1ItemStack;
                        }

                        if (--par1ItemStack.stackSize <= 0)
                        {
                            return new ItemStack(Item.bucketWater);
                        }

                        if (!par3EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Item.bucketWater)))
                        {
                            par3EntityPlayer.dropPlayerItem(new ItemStack(Item.bucketWater.itemID, 1, 0));
                        }

                        return par1ItemStack;
                    }

                    if (par2World.getBlockMaterial(var6, var7, var8) == Material.lava && par2World.getBlockMetadata(var6, var7, var8) == 0)
                    {
                        par2World.setBlockToAir(var6, var7, var8);

                        if (par3EntityPlayer.capabilities.isCreativeMode)
                        {
                            return par1ItemStack;
                        }

                        if (--par1ItemStack.stackSize <= 0)
                        {
                            return new ItemStack(Item.bucketLava);
                        }

                        if (!par3EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Item.bucketLava)))
                        {
                            par3EntityPlayer.dropPlayerItem(new ItemStack(Item.bucketLava.itemID, 1, 0));
                        }

                        return par1ItemStack;
                    }
                }
                else
                {
                    if (this.isFull < 0)
                    {
                        return new ItemStack(Item.bucketEmpty);
                    }

                    if (var5.sideHit == 0)
                    {
                        --var7;
                    }

                    if (var5.sideHit == 1)
                    {
                        ++var7;
                    }

                    if (var5.sideHit == 2)
                    {
                        --var8;
                    }

                    if (var5.sideHit == 3)
                    {
                        ++var8;
                    }

                    if (var5.sideHit == 4)
                    {
                        --var6;
                    }

                    if (var5.sideHit == 5)
                    {
                        ++var6;
                    }

                    if (!par3EntityPlayer.canPlayerEdit(var6, var7, var8, var5.sideHit, par1ItemStack))
                    {
                        return par1ItemStack;
                    }

                    if (this.tryPlaceContainedLiquid(par2World, var6, var7, var8) && !par3EntityPlayer.capabilities.isCreativeMode)
                    {
                        return new ItemStack(Item.bucketEmpty);
                    }
                }
            }

            return par1ItemStack;
        }
    }

    /**
     * Attempts to place the liquid contained inside the bucket.
     */
    public boolean tryPlaceContainedLiquid(World par1World, int par2, int par3, int par4)
    {
        if (this.isFull <= 0)
        {
            return false;
        }
        else
        {
            Material var5 = par1World.getBlockMaterial(par2, par3, par4);
            boolean var6 = !var5.isSolid();

            if (!par1World.isAirBlock(par2, par3, par4) && !var6)
            {
                return false;
            }
            else
            {
                if (par1World.provider.isHellWorld && this.isFull == Block.waterMoving.blockID)
                {
                    par1World.playSoundEffect((double)((float)par2 + 0.5F), (double)((float)par3 + 0.5F), (double)((float)par4 + 0.5F), "random.fizz", 0.5F, 2.6F + (par1World.rand.nextFloat() - par1World.rand.nextFloat()) * 0.8F);

                    for (int var7 = 0; var7 < 8; ++var7)
                    {
                        par1World.spawnParticle("largesmoke", (double)par2 + Math.random(), (double)par3 + Math.random(), (double)par4 + Math.random(), 0.0D, 0.0D, 0.0D);
                    }
                }
                else
                {
                    if (!par1World.isRemote && var6 && !var5.isLiquid())
                    {
                        par1World.destroyBlock(par2, par3, par4, true);
                    }

                    par1World.setBlock(par2, par3, par4, this.isFull, 0, 3);
                }

                return true;
            }
        }
    }
}
