package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EnchantmentDamage extends Enchantment
{
    private static final String[] protectionName = new String[] {"all", "undead", "arthropods"};
    private static final int[] baseEnchantability = new int[] {1, 5, 5};
    private static final int[] levelEnchantability = new int[] {11, 8, 8};
    private static final int[] thresholdEnchantability = new int[] {20, 20, 20};
    public final int damageType;
    private static final String __OBFID = "CL_00000102";

    public EnchantmentDamage(int par1, int par2, int par3)
    {
        super(par1, par2, EnumEnchantmentType.weapon);
        this.damageType = par3;
    }

    public int getMinEnchantability(int par1)
    {
        return baseEnchantability[this.damageType] + (par1 - 1) * levelEnchantability[this.damageType];
    }

    public int getMaxEnchantability(int par1)
    {
        return this.getMinEnchantability(par1) + thresholdEnchantability[this.damageType];
    }

    public int getMaxLevel()
    {
        return 5;
    }

    public float calcModifierLiving(int par1, EntityLivingBase par2EntityLivingBase)
    {
        return this.damageType == 0 ? (float)par1 * 1.25F : (this.damageType == 1 && par2EntityLivingBase.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD ? (float)par1 * 2.5F : (this.damageType == 2 && par2EntityLivingBase.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD ? (float)par1 * 2.5F : 0.0F));
    }

    public String getName()
    {
        return "enchantment.damage." + protectionName[this.damageType];
    }

    public boolean canApplyTogether(Enchantment par1Enchantment)
    {
        return !(par1Enchantment instanceof EnchantmentDamage);
    }

    public boolean canApply(ItemStack par1ItemStack)
    {
        return par1ItemStack.getItem() instanceof ItemAxe ? true : super.canApply(par1ItemStack);
    }

    public void func_151368_a(EntityLivingBase p_151368_1_, Entity p_151368_2_, int p_151368_3_)
    {
        if (p_151368_2_ instanceof EntityLivingBase)
        {
            EntityLivingBase var4 = (EntityLivingBase)p_151368_2_;

            if (this.damageType == 2 && var4.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD)
            {
                int var5 = 20 + p_151368_1_.getRNG().nextInt(10 * p_151368_3_);
                var4.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, var5, 3));
            }
        }
    }
}
