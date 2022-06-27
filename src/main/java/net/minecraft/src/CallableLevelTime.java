package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableLevelTime implements Callable
{
    final WorldInfo worldInfoInstance;

    CallableLevelTime(WorldInfo par1WorldInfo)
    {
        this.worldInfoInstance = par1WorldInfo;
    }

    public String callLevelTime()
    {
        return String.format("%d game time, %d day time", new Object[] {Long.valueOf(WorldInfo.func_85126_g(this.worldInfoInstance)), Long.valueOf(WorldInfo.getWorldTime(this.worldInfoInstance))});
    }

    public Object call()
    {
        return this.callLevelTime();
    }
}
