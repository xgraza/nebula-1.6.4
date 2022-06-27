package net.minecraft.src;

class VillageAgressor
{
    public EntityLivingBase agressor;
    public int agressionTime;

    final Village villageObj;

    VillageAgressor(Village par1Village, EntityLivingBase par2EntityLivingBase, int par3)
    {
        this.villageObj = par1Village;
        this.agressor = par2EntityLivingBase;
        this.agressionTime = par3;
    }
}
