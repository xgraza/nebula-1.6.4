package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableTexturePack implements Callable
{
    final Minecraft theMinecraft;

    CallableTexturePack(Minecraft par1Minecraft)
    {
        this.theMinecraft = par1Minecraft;
    }

    public String callTexturePack()
    {
        return this.theMinecraft.gameSettings.skin;
    }

    public Object call()
    {
        return this.callTexturePack();
    }
}
