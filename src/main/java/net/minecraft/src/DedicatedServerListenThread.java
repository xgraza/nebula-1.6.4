package net.minecraft.src;

import java.io.IOException;
import java.net.InetAddress;
import net.minecraft.server.MinecraftServer;

public class DedicatedServerListenThread extends NetworkListenThread
{
    /** Instance of ServerListenThread. */
    private final ServerListenThread theServerListenThread;

    public DedicatedServerListenThread(MinecraftServer par1MinecraftServer, InetAddress par2InetAddress, int par3) throws IOException
    {
        super(par1MinecraftServer);
        this.theServerListenThread = new ServerListenThread(this, par2InetAddress, par3);
        this.theServerListenThread.start();
    }

    public void stopListening()
    {
        super.stopListening();
        this.theServerListenThread.func_71768_b();
        this.theServerListenThread.interrupt();
    }

    /**
     * processes packets and pending connections
     */
    public void networkTick()
    {
        this.theServerListenThread.processPendingConnections();
        super.networkTick();
    }

    public DedicatedServer getDedicatedServer()
    {
        return (DedicatedServer)super.getServer();
    }

    public void func_71761_a(InetAddress par1InetAddress)
    {
        this.theServerListenThread.func_71769_a(par1InetAddress);
    }

    public MinecraftServer getServer()
    {
        return this.getDedicatedServer();
    }
}
