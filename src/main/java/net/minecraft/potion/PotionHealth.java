package net.minecraft.potion;

public class PotionHealth extends Potion
{
    private static final String __OBFID = "CL_00001527";

    public PotionHealth(int par1, boolean par2, int par3)
    {
        super(par1, par2, par3);
    }

    public boolean isInstant()
    {
        return true;
    }

    public boolean isReady(int par1, int par2)
    {
        return par1 >= 1;
    }
}