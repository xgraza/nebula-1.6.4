package net.minecraft.src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ThreadLanServerPing extends Thread
{
    private final String motd;

    /** The socket we're using to send packets on. */
    private final DatagramSocket socket;
    private boolean isStopping = true;
    private final String address;

    public ThreadLanServerPing(String par1Str, String par2Str) throws IOException
    {
        super("LanServerPinger");
        this.motd = par1Str;
        this.address = par2Str;
        this.setDaemon(true);
        this.socket = new DatagramSocket();
    }

    public void run()
    {
        String var1 = getPingResponse(this.motd, this.address);
        byte[] var2 = var1.getBytes();

        while (!this.isInterrupted() && this.isStopping)
        {
            try
            {
                InetAddress var3 = InetAddress.getByName("224.0.2.60");
                DatagramPacket var4 = new DatagramPacket(var2, var2.length, var3, 4445);
                this.socket.send(var4);
            }
            catch (IOException var6)
            {
                Minecraft.getMinecraft().getLogAgent().logWarning("LanServerPinger: " + var6.getMessage());
                break;
            }

            try
            {
                sleep(1500L);
            }
            catch (InterruptedException var5)
            {
                ;
            }
        }
    }

    public void interrupt()
    {
        super.interrupt();
        this.isStopping = false;
    }

    public static String getPingResponse(String par0Str, String par1Str)
    {
        return "[MOTD]" + par0Str + "[/MOTD][AD]" + par1Str + "[/AD]";
    }

    public static String getMotdFromPingResponse(String par0Str)
    {
        int var1 = par0Str.indexOf("[MOTD]");

        if (var1 < 0)
        {
            return "missing no";
        }
        else
        {
            int var2 = par0Str.indexOf("[/MOTD]", var1 + "[MOTD]".length());
            return var2 < var1 ? "missing no" : par0Str.substring(var1 + "[MOTD]".length(), var2);
        }
    }

    public static String getAdFromPingResponse(String par0Str)
    {
        int var1 = par0Str.indexOf("[/MOTD]");

        if (var1 < 0)
        {
            return null;
        }
        else
        {
            int var2 = par0Str.indexOf("[/MOTD]", var1 + "[/MOTD]".length());

            if (var2 >= 0)
            {
                return null;
            }
            else
            {
                int var3 = par0Str.indexOf("[AD]", var1 + "[/MOTD]".length());

                if (var3 < 0)
                {
                    return null;
                }
                else
                {
                    int var4 = par0Str.indexOf("[/AD]", var3 + "[AD]".length());
                    return var4 < var3 ? null : par0Str.substring(var3 + "[AD]".length(), var4);
                }
            }
        }
    }
}
