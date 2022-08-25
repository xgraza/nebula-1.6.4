package net.minecraft.item;

public class ItemBook extends Item
{
    private static final String __OBFID = "CL_00001775";

    public boolean isItemTool(ItemStack par1ItemStack)
    {
        return par1ItemStack.stackSize == 1;
    }

    public int getItemEnchantability()
    {
        return 1;
    }
}
