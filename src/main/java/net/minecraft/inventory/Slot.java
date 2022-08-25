package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class Slot
{
    private final int slotIndex;
    public final IInventory inventory;
    public int slotNumber;
    public int xDisplayPosition;
    public int yDisplayPosition;
    private static final String __OBFID = "CL_00001762";

    public Slot(IInventory par1IInventory, int par2, int par3, int par4)
    {
        this.inventory = par1IInventory;
        this.slotIndex = par2;
        this.xDisplayPosition = par3;
        this.yDisplayPosition = par4;
    }

    public void onSlotChange(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        if (par1ItemStack != null && par2ItemStack != null)
        {
            if (par1ItemStack.getItem() == par2ItemStack.getItem())
            {
                int var3 = par2ItemStack.stackSize - par1ItemStack.stackSize;

                if (var3 > 0)
                {
                    this.onCrafting(par1ItemStack, var3);
                }
            }
        }
    }

    protected void onCrafting(ItemStack par1ItemStack, int par2) {}

    protected void onCrafting(ItemStack par1ItemStack) {}

    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
        this.onSlotChanged();
    }

    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return true;
    }

    public ItemStack getStack()
    {
        return this.inventory.getStackInSlot(this.slotIndex);
    }

    public boolean getHasStack()
    {
        return this.getStack() != null;
    }

    public void putStack(ItemStack par1ItemStack)
    {
        this.inventory.setInventorySlotContents(this.slotIndex, par1ItemStack);
        this.onSlotChanged();
    }

    public void onSlotChanged()
    {
        this.inventory.onInventoryChanged();
    }

    public int getSlotStackLimit()
    {
        return this.inventory.getInventoryStackLimit();
    }

    public IIcon getBackgroundIconIndex()
    {
        return null;
    }

    public ItemStack decrStackSize(int par1)
    {
        return this.inventory.decrStackSize(this.slotIndex, par1);
    }

    public boolean isSlotInInventory(IInventory par1IInventory, int par2)
    {
        return par1IInventory == this.inventory && par2 == this.slotIndex;
    }

    public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    public boolean func_111238_b()
    {
        return true;
    }
}
