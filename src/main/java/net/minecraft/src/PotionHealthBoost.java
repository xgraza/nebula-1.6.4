package net.minecraft.src;

public class PotionHealthBoost extends Potion
{
    public PotionHealthBoost(int par1, boolean par2, int par3)
    {
        super(par1, par2, par3);
    }

    public void removeAttributesModifiersFromEntity(EntityLivingBase par1EntityLivingBase, BaseAttributeMap par2BaseAttributeMap, int par3)
    {
        super.removeAttributesModifiersFromEntity(par1EntityLivingBase, par2BaseAttributeMap, par3);

        if (par1EntityLivingBase.getHealth() > par1EntityLivingBase.getMaxHealth())
        {
            par1EntityLivingBase.setHealth(par1EntityLivingBase.getMaxHealth());
        }
    }
}
