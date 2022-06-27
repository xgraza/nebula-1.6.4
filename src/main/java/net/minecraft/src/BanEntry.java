package net.minecraft.src;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import net.minecraft.server.MinecraftServer;

public class BanEntry
{
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private final String username;
    private Date banStartDate = new Date();
    private String bannedBy = "(Unknown)";
    private Date banEndDate;
    private String reason = "Banned by an operator.";

    public BanEntry(String par1Str)
    {
        this.username = par1Str;
    }

    public String getBannedUsername()
    {
        return this.username;
    }

    public Date getBanStartDate()
    {
        return this.banStartDate;
    }

    /**
     * null == start ban now
     */
    public void setBanStartDate(Date par1Date)
    {
        this.banStartDate = par1Date != null ? par1Date : new Date();
    }

    public String getBannedBy()
    {
        return this.bannedBy;
    }

    public void setBannedBy(String par1Str)
    {
        this.bannedBy = par1Str;
    }

    public Date getBanEndDate()
    {
        return this.banEndDate;
    }

    public void setBanEndDate(Date par1Date)
    {
        this.banEndDate = par1Date;
    }

    public boolean hasBanExpired()
    {
        return this.banEndDate == null ? false : this.banEndDate.before(new Date());
    }

    public String getBanReason()
    {
        return this.reason;
    }

    public void setBanReason(String par1Str)
    {
        this.reason = par1Str;
    }

    public String buildBanString()
    {
        StringBuilder var1 = new StringBuilder();
        var1.append(this.getBannedUsername());
        var1.append("|");
        var1.append(dateFormat.format(this.getBanStartDate()));
        var1.append("|");
        var1.append(this.getBannedBy());
        var1.append("|");
        var1.append(this.getBanEndDate() == null ? "Forever" : dateFormat.format(this.getBanEndDate()));
        var1.append("|");
        var1.append(this.getBanReason());
        return var1.toString();
    }

    public static BanEntry parse(String par0Str)
    {
        if (par0Str.trim().length() < 2)
        {
            return null;
        }
        else
        {
            String[] var1 = par0Str.trim().split(Pattern.quote("|"), 5);
            BanEntry var2 = new BanEntry(var1[0].trim());
            byte var3 = 0;
            int var10000 = var1.length;
            int var7 = var3 + 1;

            if (var10000 <= var7)
            {
                return var2;
            }
            else
            {
                try
                {
                    var2.setBanStartDate(dateFormat.parse(var1[var7].trim()));
                }
                catch (ParseException var6)
                {
                    MinecraftServer.getServer().getLogAgent().logWarningException("Could not read creation date format for ban entry \'" + var2.getBannedUsername() + "\' (was: \'" + var1[var7] + "\')", var6);
                }

                var10000 = var1.length;
                ++var7;

                if (var10000 <= var7)
                {
                    return var2;
                }
                else
                {
                    var2.setBannedBy(var1[var7].trim());
                    var10000 = var1.length;
                    ++var7;

                    if (var10000 <= var7)
                    {
                        return var2;
                    }
                    else
                    {
                        try
                        {
                            String var4 = var1[var7].trim();

                            if (!var4.equalsIgnoreCase("Forever") && var4.length() > 0)
                            {
                                var2.setBanEndDate(dateFormat.parse(var4));
                            }
                        }
                        catch (ParseException var5)
                        {
                            MinecraftServer.getServer().getLogAgent().logWarningException("Could not read expiry date format for ban entry \'" + var2.getBannedUsername() + "\' (was: \'" + var1[var7] + "\')", var5);
                        }

                        var10000 = var1.length;
                        ++var7;

                        if (var10000 <= var7)
                        {
                            return var2;
                        }
                        else
                        {
                            var2.setBanReason(var1[var7].trim());
                            return var2;
                        }
                    }
                }
            }
        }
    }
}
