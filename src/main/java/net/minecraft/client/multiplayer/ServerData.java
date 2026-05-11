package net.minecraft.client.multiplayer;

import net.minecraft.nbt.NBTTagCompound;

public class ServerData
{
    public String serverName;
    public String serverIP;

    /**
     * the string indicating number of players on and capacity of the server that is shown on the server browser (i.e.
     * "5/20" meaning 5 slots used out of 20 slots total)
     */
    public String populationInfo;

    /**
     * (better variable name would be 'hostname') server name as displayed in the server browser's second line (grey
     * text)
     */
    public String serverMOTD;

    /**
     * last server ping that showed up in the server browser
     */
    public long pingToServer;
    public int field_82821_f = 4;

    /**
     * Game version for this server.
     */
    public String gameVersion = "1.7.2";
    public boolean field_78841_f;
    public String field_147412_i;
    private boolean field_78842_g = true;
    private boolean acceptsTextures;

    /**
     * Whether to hide the IP address for this server.
     */
    private boolean hideAddress;
    private String icon;

    public ServerData(String par1Str, String par2Str)
    {
        this.serverName = par1Str;
        this.serverIP = par2Str;
    }

    /**
     * Returns an NBTTagCompound with the server's name, IP and maybe acceptTextures.
     */
    public NBTTagCompound getNBTCompound()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        var1.setString("name", this.serverName);
        var1.setString("ip", this.serverIP);
        var1.setBoolean("hideAddress", this.hideAddress);

        if (this.icon != null)
        {
            var1.setString("icon", this.icon);
        }

        if (!this.field_78842_g)
        {
            var1.setBoolean("acceptTextures", this.acceptsTextures);
        }

        return var1;
    }

    public boolean acceptsTextures()
    {
        return this.acceptsTextures;
    }

    public boolean func_147410_c()
    {
        return this.field_78842_g;
    }

    public void setAcceptsTextures(boolean par1)
    {
        this.acceptsTextures = par1;
        this.field_78842_g = false;
    }

    public boolean isHidingAddress()
    {
        return this.hideAddress;
    }

    public void setHideAddress(boolean par1)
    {
        this.hideAddress = par1;
    }

    /**
     * Takes an NBTTagCompound with 'name' and 'ip' keys, returns a ServerData instance.
     */
    public static ServerData getServerDataFromNBTCompound(NBTTagCompound compound)
    {
        ServerData data = new ServerData(compound.getString("name"), compound.getString("ip"));
        data.hideAddress = compound.getBoolean("hideAddress");

        if (compound.hasKey("icon", 8))
        {
            data.setIcon(compound.getString("icon"));
        }

        if (compound.hasKey("acceptTextures", 99))
        {
            data.setAcceptsTextures(compound.getBoolean("acceptTextures"));
        }

        return data;
    }

    public String getIcon()
    {
        return this.icon;
    }

    public void setIcon(String p_147407_1_)
    {
        this.icon = p_147407_1_;
    }

    public static ServerData of(final String address)
    {
        return new ServerData("", address);
    }
}
