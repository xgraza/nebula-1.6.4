package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableOSInfo implements Callable
{
    /** Reference to the CrashReport object. */
    final CrashReport theCrashReport;

    CallableOSInfo(CrashReport par1CrashReport)
    {
        this.theCrashReport = par1CrashReport;
    }

    public String getOsAsString()
    {
        return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
    }

    public Object call()
    {
        return this.getOsAsString();
    }
}
