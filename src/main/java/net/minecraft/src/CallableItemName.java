package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableItemName implements Callable
{
    final ItemStack theItemStack;

    final InventoryPlayer playerInventory;

    CallableItemName(InventoryPlayer par1InventoryPlayer, ItemStack par2ItemStack)
    {
        this.playerInventory = par1InventoryPlayer;
        this.theItemStack = par2ItemStack;
    }

    public String callItemDisplayName()
    {
        return this.theItemStack.getDisplayName();
    }

    public Object call()
    {
        return this.callItemDisplayName();
    }
}
