package net.minecraft.src;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LanServerList
{
    private ArrayList listOfLanServers = new ArrayList();
    boolean wasUpdated;

    public synchronized boolean getWasUpdated()
    {
        return this.wasUpdated;
    }

    public synchronized void setWasNotUpdated()
    {
        this.wasUpdated = false;
    }

    public synchronized List getLanServers()
    {
        return Collections.unmodifiableList(this.listOfLanServers);
    }

    public synchronized void func_77551_a(String par1Str, InetAddress par2InetAddress)
    {
        String var3 = ThreadLanServerPing.getMotdFromPingResponse(par1Str);
        String var4 = ThreadLanServerPing.getAdFromPingResponse(par1Str);

        if (var4 != null)
        {
            var4 = par2InetAddress.getHostAddress() + ":" + var4;
            boolean var5 = false;
            Iterator var6 = this.listOfLanServers.iterator();

            while (var6.hasNext())
            {
                LanServer var7 = (LanServer)var6.next();

                if (var7.getServerIpPort().equals(var4))
                {
                    var7.updateLastSeen();
                    var5 = true;
                    break;
                }
            }

            if (!var5)
            {
                this.listOfLanServers.add(new LanServer(var3, var4));
                this.wasUpdated = true;
            }
        }
    }
}
