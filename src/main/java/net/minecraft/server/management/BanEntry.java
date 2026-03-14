package net.minecraft.server.management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BanEntry
{
    private static final Logger logger = LogManager.getLogger();
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private final String username;
    private final Date banStartDate = new Date();
    private String bannedBy = "(Unknown)";
    private Date banEndDate;
    private String reason = "Banned by an operator.";
    private static final String __OBFID = "CL_00001395";

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

    public boolean hasBanExpired()
    {
        return this.banEndDate != null && this.banEndDate.before(new Date());
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
        String var1 = this.getBannedUsername() +
                "|" +
                dateFormat.format(this.getBanStartDate()) +
                "|" +
                this.getBannedBy() +
                "|" +
                (this.getBanEndDate() == null ? "Forever" : dateFormat.format(this.getBanEndDate())) +
                "|" +
                this.getBanReason();
        return var1;
    }
}
