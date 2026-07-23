package net.minecraft.client.multiplayer;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServerList
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The Minecraft instance.
     */
    private final Minecraft mc;

    /**
     * List of ServerData instances.
     */
    private final List<ServerData> servers = new ArrayList<>();

    public ServerList(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
        this.loadServerList();
    }

    /**
     * Loads a list of servers from servers.dat, by running ServerData.getServerDataFromNBTCompound on each NBT compound
     * found in the "servers" tag list.
     */
    public void loadServerList()
    {
        try
        {
            this.servers.clear();
            NBTTagCompound compound = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));

            if (compound == null)
            {
                return;
            }

            NBTTagList serverTagList = compound.getTagList("servers", 10);

            for (int i = 0; i < serverTagList.tagCount(); ++i)
            {
                this.servers.add(ServerData.getServerDataFromNBTCompound(serverTagList.getCompoundTagAt(i)));
            }
        } catch (Exception var4)
        {
            LOGGER.error("Couldn't load server list", var4);
        }
    }

    /**
     * Runs getNBTCompound on each ServerData instance, puts everything into a "servers" NBT list and writes it to
     * servers.dat.
     */
    public void saveServerList()
    {
        try
        {
            NBTTagList serverTagList = new NBTTagList();
            for (ServerData var3 : this.servers)
            {
                serverTagList.appendTag(var3.getNBTCompound());
            }
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("servers", serverTagList);
            CompressedStreamTools.safeWrite(compound, new File(this.mc.mcDataDir, "servers.dat"));
        } catch (Exception var4)
        {
            LOGGER.error("Couldn't save server list", var4);
        }
    }

    /**
     * Gets the ServerData instance stored for the given index in the list.
     */
    public ServerData getServerData(int i)
    {
        return this.servers.get(i);
    }

    /**
     * Removes the ServerData instance stored for the given index in the list.
     */
    public void removeServerData(int i)
    {
        this.servers.remove(i);
    }

    /**
     * Adds the given ServerData instance to the list.
     */
    public void addServerData(ServerData serverData)
    {
        this.servers.add(serverData);
    }

    /**
     * Counts the number of ServerData instances in the list.
     */
    public int countServers()
    {
        return this.servers.size();
    }

    /**
     * Takes two list indexes, and swaps their order around.
     */
    public void swapServers(int i, int i1)
    {
        ServerData var3 = this.getServerData(i);
        this.servers.set(i, this.getServerData(i1));
        this.servers.set(i1, var3);
        this.saveServerList();
    }

    public void func_147413_a(int p_147413_1_, ServerData p_147413_2_)
    {
        this.servers.set(p_147413_1_, p_147413_2_);
    }

    public static void func_147414_b(ServerData serverData)
    {
        ServerList serverList = new ServerList(Minecraft.getMinecraft());
        serverList.loadServerList();

        for (int var2 = 0; var2 < serverList.countServers(); ++var2)
        {
            ServerData var3 = serverList.getServerData(var2);

            if (var3.serverName.equals(serverData.serverName) && var3.serverIP.equals(serverData.serverIP))
            {
                serverList.func_147413_a(var2, serverData);
                break;
            }
        }

        serverList.saveServerList();
    }
}
