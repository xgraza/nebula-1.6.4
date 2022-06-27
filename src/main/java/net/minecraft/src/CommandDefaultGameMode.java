package net.minecraft.src;

import java.util.Iterator;
import net.minecraft.server.MinecraftServer;

public class CommandDefaultGameMode extends CommandGameMode
{
    public String getCommandName()
    {
        return "defaultgamemode";
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.defaultgamemode.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length > 0)
        {
            EnumGameType var3 = this.getGameModeFromCommand(par1ICommandSender, par2ArrayOfStr[0]);
            this.setGameType(var3);
            notifyAdmins(par1ICommandSender, "commands.defaultgamemode.success", new Object[] {ChatMessageComponent.createFromTranslationKey("gameMode." + var3.getName())});
        }
        else
        {
            throw new WrongUsageException("commands.defaultgamemode.usage", new Object[0]);
        }
    }

    protected void setGameType(EnumGameType par1EnumGameType)
    {
        MinecraftServer var2 = MinecraftServer.getServer();
        var2.setGameType(par1EnumGameType);
        EntityPlayerMP var4;

        if (var2.getForceGamemode())
        {
            for (Iterator var3 = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator(); var3.hasNext(); var4.fallDistance = 0.0F)
            {
                var4 = (EntityPlayerMP)var3.next();
                var4.setGameType(par1EnumGameType);
            }
        }
    }
}
