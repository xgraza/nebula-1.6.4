package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableUpdatingScreenName implements Callable
{
    final Minecraft theMinecraft;

    CallableUpdatingScreenName(Minecraft par1Minecraft)
    {
        this.theMinecraft = par1Minecraft;
    }

    public String callUpdatingScreenName()
    {
        return this.theMinecraft.currentScreen.getClass().getCanonicalName();
    }

    public Object call()
    {
        return this.callUpdatingScreenName();
    }
}
