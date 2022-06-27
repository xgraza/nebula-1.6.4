package net.minecraft.src;

class ThreadClientSleep extends Thread
{
    /** A reference to the Minecraft object. */
    final Minecraft mc;

    ThreadClientSleep(Minecraft par1Minecraft, String par2Str)
    {
        super(par2Str);
        this.mc = par1Minecraft;
    }

    public void run()
    {
        while (this.mc.running)
        {
            try
            {
                Thread.sleep(2147483647L);
            }
            catch (InterruptedException var2)
            {
                ;
            }
        }
    }
}
