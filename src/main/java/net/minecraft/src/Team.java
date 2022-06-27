package net.minecraft.src;

public abstract class Team
{
    /**
     * Same as ==
     */
    public boolean isSameTeam(Team par1Team)
    {
        return par1Team == null ? false : this == par1Team;
    }

    public abstract String func_96661_b();

    public abstract String func_142053_d(String var1);

    public abstract boolean func_98297_h();

    public abstract boolean getAllowFriendlyFire();
}
