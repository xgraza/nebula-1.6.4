package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class CommandDeOp extends CommandBase
{
    private static final String __OBFID = "CL_00000244";

    public String getCommandName()
    {
        return "deop";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.deop.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1 && par2ArrayOfStr[0].length() > 0)
        {
            MinecraftServer.getServer().getConfigurationManager().removeOp(par2ArrayOfStr[0]);
            notifyAdmins(par1ICommandSender, "commands.deop.success", par2ArrayOfStr[0]);
        } else
        {
            throw new WrongUsageException("commands.deop.usage");
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getConfigurationManager().getOps()) : null;
    }
}
