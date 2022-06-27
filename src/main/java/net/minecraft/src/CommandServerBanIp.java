package net.minecraft.src;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.server.MinecraftServer;

public class CommandServerBanIp extends CommandBase
{
    public static final Pattern IPv4Pattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public String getCommandName()
    {
        return "ban-ip";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    /**
     * Returns true if the given command sender is allowed to use this command.
     */
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isListActive() && super.canCommandSenderUseCommand(par1ICommandSender);
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.banip.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length >= 1 && par2ArrayOfStr[0].length() > 1)
        {
            Matcher var3 = IPv4Pattern.matcher(par2ArrayOfStr[0]);
            String var4 = null;

            if (par2ArrayOfStr.length >= 2)
            {
                var4 = func_82360_a(par1ICommandSender, par2ArrayOfStr, 1);
            }

            if (var3.matches())
            {
                this.banIP(par1ICommandSender, par2ArrayOfStr[0], var4);
            }
            else
            {
                EntityPlayerMP var5 = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(par2ArrayOfStr[0]);

                if (var5 == null)
                {
                    throw new PlayerNotFoundException("commands.banip.invalid", new Object[0]);
                }

                this.banIP(par1ICommandSender, var5.getPlayerIP(), var4);
            }
        }
        else
        {
            throw new WrongUsageException("commands.banip.usage", new Object[0]);
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames()) : null;
    }

    /**
     * Actually does the banning work.
     */
    protected void banIP(ICommandSender par1ICommandSender, String par2Str, String par3Str)
    {
        BanEntry var4 = new BanEntry(par2Str);
        var4.setBannedBy(par1ICommandSender.getCommandSenderName());

        if (par3Str != null)
        {
            var4.setBanReason(par3Str);
        }

        MinecraftServer.getServer().getConfigurationManager().getBannedIPs().put(var4);
        List var5 = MinecraftServer.getServer().getConfigurationManager().getPlayerList(par2Str);
        String[] var6 = new String[var5.size()];
        int var7 = 0;
        EntityPlayerMP var9;

        for (Iterator var8 = var5.iterator(); var8.hasNext(); var6[var7++] = var9.getEntityName())
        {
            var9 = (EntityPlayerMP)var8.next();
            var9.playerNetServerHandler.kickPlayerFromServer("You have been IP banned.");
        }

        if (var5.isEmpty())
        {
            notifyAdmins(par1ICommandSender, "commands.banip.success", new Object[] {par2Str});
        }
        else
        {
            notifyAdmins(par1ICommandSender, "commands.banip.success.players", new Object[] {par2Str, joinNiceString(var6)});
        }
    }
}
