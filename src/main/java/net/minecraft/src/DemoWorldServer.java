package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class DemoWorldServer extends WorldServer
{
    private static final long demoWorldSeed = (long)"North Carolina".hashCode();
    public static final WorldSettings demoWorldSettings = (new WorldSettings(demoWorldSeed, EnumGameType.SURVIVAL, true, false, WorldType.DEFAULT)).enableBonusChest();

    public DemoWorldServer(MinecraftServer par1MinecraftServer, ISaveHandler par2ISaveHandler, String par3Str, int par4, Profiler par5Profiler, ILogAgent par6ILogAgent)
    {
        super(par1MinecraftServer, par2ISaveHandler, par3Str, par4, demoWorldSettings, par5Profiler, par6ILogAgent);
    }
}
