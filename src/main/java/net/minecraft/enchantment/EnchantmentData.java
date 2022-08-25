package net.minecraft.enchantment;

import net.minecraft.util.WeightedRandom;

public class EnchantmentData extends WeightedRandom.Item
{
    public final Enchantment enchantmentobj;
    public final int enchantmentLevel;
    private static final String __OBFID = "CL_00000115";

    public EnchantmentData(Enchantment par1Enchantment, int par2)
    {
        super(par1Enchantment.getWeight());
        this.enchantmentobj = par1Enchantment;
        this.enchantmentLevel = par2;
    }

    public EnchantmentData(int par1, int par2)
    {
        this(Enchantment.enchantmentsList[par1], par2);
    }
}
