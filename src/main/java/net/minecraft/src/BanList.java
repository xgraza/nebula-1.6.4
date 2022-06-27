package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.server.MinecraftServer;

public class BanList
{
    private final LowerStringMap theBanList = new LowerStringMap();
    private final File fileName;

    /** set to true if not singlePlayer */
    private boolean listActive = true;

    public BanList(File par1File)
    {
        this.fileName = par1File;
    }

    public boolean isListActive()
    {
        return this.listActive;
    }

    public void setListActive(boolean par1)
    {
        this.listActive = par1;
    }

    /**
     * removes expired Bans before returning
     */
    public Map getBannedList()
    {
        this.removeExpiredBans();
        return this.theBanList;
    }

    public boolean isBanned(String par1Str)
    {
        if (!this.isListActive())
        {
            return false;
        }
        else
        {
            this.removeExpiredBans();
            return this.theBanList.containsKey(par1Str);
        }
    }

    public void put(BanEntry par1BanEntry)
    {
        this.theBanList.putLower(par1BanEntry.getBannedUsername(), par1BanEntry);
        this.saveToFileWithHeader();
    }

    public void remove(String par1Str)
    {
        this.theBanList.remove(par1Str);
        this.saveToFileWithHeader();
    }

    public void removeExpiredBans()
    {
        Iterator var1 = this.theBanList.values().iterator();

        while (var1.hasNext())
        {
            BanEntry var2 = (BanEntry)var1.next();

            if (var2.hasBanExpired())
            {
                var1.remove();
            }
        }
    }

    /**
     * Loads the ban list from the file (adds every entry, does not clear the current list).
     */
    public void loadBanList()
    {
        if (this.fileName.isFile())
        {
            BufferedReader var1;

            try
            {
                var1 = new BufferedReader(new FileReader(this.fileName));
            }
            catch (FileNotFoundException var4)
            {
                throw new Error();
            }

            String var2;

            try
            {
                while ((var2 = var1.readLine()) != null)
                {
                    if (!var2.startsWith("#"))
                    {
                        BanEntry var3 = BanEntry.parse(var2);

                        if (var3 != null)
                        {
                            this.theBanList.putLower(var3.getBannedUsername(), var3);
                        }
                    }
                }
            }
            catch (IOException var5)
            {
                MinecraftServer.getServer().getLogAgent().logSevereException("Could not load ban list", var5);
            }
        }
    }

    public void saveToFileWithHeader()
    {
        this.saveToFile(true);
    }

    /**
     * par1: include header
     */
    public void saveToFile(boolean par1)
    {
        this.removeExpiredBans();

        try
        {
            PrintWriter var2 = new PrintWriter(new FileWriter(this.fileName, false));

            if (par1)
            {
                var2.println("# Updated " + (new SimpleDateFormat()).format(new Date()) + " by Minecraft " + "1.6.4");
                var2.println("# victim name | ban date | banned by | banned until | reason");
                var2.println();
            }

            Iterator var3 = this.theBanList.values().iterator();

            while (var3.hasNext())
            {
                BanEntry var4 = (BanEntry)var3.next();
                var2.println(var4.buildBanString());
            }

            var2.close();
        }
        catch (IOException var5)
        {
            MinecraftServer.getServer().getLogAgent().logSevereException("Could not save ban list", var5);
        }
    }
}
