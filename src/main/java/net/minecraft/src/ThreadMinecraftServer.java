package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class ThreadMinecraftServer extends Thread
{
    /** Instance of MinecraftServer. */
    final MinecraftServer theServer;

    public ThreadMinecraftServer(MinecraftServer par1MinecraftServer, String par2Str)
    {
        super(par2Str);
        this.theServer = par1MinecraftServer;
    }

    public void run()
    {
        this.theServer.run();
    }
}
