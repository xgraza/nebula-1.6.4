package net.minecraft.src;

public final class MainShutdownHook extends Thread
{
    public void run()
    {
        Minecraft.stopIntegratedServer();
    }
}
