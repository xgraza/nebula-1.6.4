package net.minecraft.enchantment;

public class EnchantmentOxygen extends Enchantment
{
    private static final String __OBFID = "CL_00000120";

    public EnchantmentOxygen(int par1, int par2)
    {
        super(par1, par2, EnumEnchantmentType.armor_head);
        this.setName("oxygen");
    }

    public int getMinEnchantability(int par1)
    {
        return 10 * par1;
    }

    public int getMaxEnchantability(int par1)
    {
        return this.getMinEnchantability(par1) + 30;
    }

    public int getMaxLevel()
    {
        return 3;
    }
}
