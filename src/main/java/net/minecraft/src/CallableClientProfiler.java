package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableClientProfiler implements Callable
{
    final Minecraft theMinecraft;

    CallableClientProfiler(Minecraft par1Minecraft)
    {
        this.theMinecraft = par1Minecraft;
    }

    public String callClientProfilerInfo()
    {
        return Minecraft.func_142024_b(this.theMinecraft).getCurrentLanguage().toString();
    }

    public Object call()
    {
        return this.callClientProfilerInfo();
    }
}
