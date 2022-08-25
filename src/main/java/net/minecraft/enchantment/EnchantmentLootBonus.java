package net.minecraft.enchantment;

public class EnchantmentLootBonus extends Enchantment
{
    private static final String __OBFID = "CL_00000119";

    protected EnchantmentLootBonus(int par1, int par2, EnumEnchantmentType par3EnumEnchantmentType)
    {
        super(par1, par2, par3EnumEnchantmentType);

        if (par3EnumEnchantmentType == EnumEnchantmentType.digger)
        {
            this.setName("lootBonusDigger");
        }
        else if (par3EnumEnchantmentType == EnumEnchantmentType.fishing_rod)
        {
            this.setName("lootBonusFishing");
        }
        else
        {
            this.setName("lootBonus");
        }
    }

    public int getMinEnchantability(int par1)
    {
        return 15 + (par1 - 1) * 9;
    }

    public int getMaxEnchantability(int par1)
    {
        return super.getMinEnchantability(par1) + 50;
    }

    public int getMaxLevel()
    {
        return 3;
    }

    public boolean canApplyTogether(Enchantment par1Enchantment)
    {
        return super.canApplyTogether(par1Enchantment) && par1Enchantment.effectId != silkTouch.effectId;
    }
}
