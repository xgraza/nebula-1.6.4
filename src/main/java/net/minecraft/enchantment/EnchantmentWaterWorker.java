package net.minecraft.enchantment;

public class EnchantmentWaterWorker extends Enchantment
{
    private static final String __OBFID = "CL_00000124";

    public EnchantmentWaterWorker(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.armor_head);
        this.setName("waterWorker");
    }

    public int getMinEnchantability(int par1)
    {
        return 1;
    }

    public int getMaxEnchantability(int par1)
    {
        return this.getMinEnchantability(par1) + 40;
    }

    public int getMaxLevel()
    {
        return 1;
    }
}
