package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableParticleScreenName implements Callable
{
    final Minecraft theMinecraft;

    CallableParticleScreenName(Minecraft par1Minecraft)
    {
        this.theMinecraft = par1Minecraft;
    }

    public String callParticleScreenName()
    {
        return this.theMinecraft.currentScreen.getClass().getCanonicalName();
    }

    public Object call()
    {
        return this.callParticleScreenName();
    }
}
