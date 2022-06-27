package net.minecraft.src;

import java.util.concurrent.Callable;
import net.minecraft.client.ClientBrandRetriever;

class CallableModded implements Callable
{
    /** Reference to the Minecraft object. */
    final Minecraft mc;

    CallableModded(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
    }

    /**
     * Gets if Client Profiler (aka Snooper) is enabled.
     */
    public String getClientProfilerEnabled()
    {
        String var1 = ClientBrandRetriever.getClientModName();
        return !var1.equals("vanilla") ? "Definitely; Client brand changed to \'" + var1 + "\'" : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.");
    }

    public Object call()
    {
        return this.getClientProfilerEnabled();
    }
}
