package net.minecraft.src;

class ContainerHorseInventorySlotArmor extends Slot
{
    final EntityHorse theHorse;

    final ContainerHorseInventory field_111240_b;

    ContainerHorseInventorySlotArmor(ContainerHorseInventory par1ContainerHorseInventory, IInventory par2IInventory, int par3, int par4, int par5, EntityHorse par6EntityHorse)
    {
        super(par2IInventory, par3, par4, par5);
        this.field_111240_b = par1ContainerHorseInventory;
        this.theHorse = par6EntityHorse;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return super.isItemValid(par1ItemStack) && this.theHorse.func_110259_cr() && EntityHorse.func_110211_v(par1ItemStack.itemID);
    }

    public boolean func_111238_b()
    {
        return this.theHorse.func_110259_cr();
    }
}
