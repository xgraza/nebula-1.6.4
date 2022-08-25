package net.minecraft.client.multiplayer;

import net.minecraft.nbt.NBTTagCompound;

public class ServerData
{
    public String serverName;
    public String serverIP;
    public String populationInfo;
    public String serverMOTD;
    public long pingToServer;
    public int field_82821_f = 4;
    public String gameVersion = "1.7.2";
    public boolean field_78841_f;
    public String field_147412_i;
    private boolean field_78842_g = true;
    private boolean acceptsTextures;
    private boolean hideAddress;
    private String field_147411_m;
    private static final String __OBFID = "CL_00000890";

    public ServerData(String par1Str, String par2Str)
    {
        this.serverName = par1Str;
        this.serverIP = par2Str;
    }

    public NBTTagCompound getNBTCompound()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        var1.setString("name", this.serverName);
        var1.setString("ip", this.serverIP);
        var1.setBoolean("hideAddress", this.hideAddress);

        if (this.field_147411_m != null)
        {
            var1.setString("icon", this.field_147411_m);
        }

        if (!this.field_78842_g)
        {
            var1.setBoolean("acceptTextures", this.acceptsTextures);
        }

        return var1;
    }

    public boolean func_147408_b()
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

    public static ServerData getServerDataFromNBTCompound(NBTTagCompound par0NBTTagCompound)
    {
        ServerData var1 = new ServerData(par0NBTTagCompound.getString("name"), par0NBTTagCompound.getString("ip"));
        var1.hideAddress = par0NBTTagCompound.getBoolean("hideAddress");

        if (par0NBTTagCompound.hasKey("icon", 8))
        {
            var1.func_147407_a(par0NBTTagCompound.getString("icon"));
        }

        if (par0NBTTagCompound.hasKey("acceptTextures", 99))
        {
            var1.setAcceptsTextures(par0NBTTagCompound.getBoolean("acceptTextures"));
        }

        return var1;
    }

    public String func_147409_e()
    {
        return this.field_147411_m;
    }

    public void func_147407_a(String p_147407_1_)
    {
        this.field_147411_m = p_147407_1_;
    }
}
