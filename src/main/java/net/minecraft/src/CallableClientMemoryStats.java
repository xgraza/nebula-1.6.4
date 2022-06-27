package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableClientMemoryStats implements Callable
{
    final Minecraft theMinecraft;

    CallableClientMemoryStats(Minecraft par1Minecraft)
    {
        this.theMinecraft = par1Minecraft;
    }

    public String callClientMemoryStats()
    {
        return this.theMinecraft.mcProfiler.profilingEnabled ? this.theMinecraft.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
    }

    public Object call()
    {
        return this.callClientMemoryStats();
    }
}
