package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableLaunchedVersion implements Callable
{
    /** Reference to the Minecraft object. */
    final Minecraft mc;

    CallableLaunchedVersion(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
    }

    public String getLWJGLVersion()
    {
        return Minecraft.getLaunchedVersion(this.mc);
    }

    public Object call()
    {
        return this.getLWJGLVersion();
    }
}
