package net.minecraft.src;

public class CommandKill extends CommandBase
{
    public String getCommandName()
    {
        return "kill";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.kill.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        EntityPlayerMP var3 = getCommandSenderAsPlayer(par1ICommandSender);
        var3.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
        par1ICommandSender.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("commands.kill.success"));
    }
}
