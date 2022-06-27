package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableIntCache implements Callable
{
    final CrashReport theCrashReport;

    CallableIntCache(CrashReport par1CrashReport)
    {
        this.theCrashReport = par1CrashReport;
    }

    public String func_85083_a()
    {
        return IntCache.func_85144_b();
    }

    public Object call()
    {
        return this.func_85083_a();
    }
}
