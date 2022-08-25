package net.minecraft.command;

import java.util.List;

public interface ICommand extends Comparable
{
    String getCommandName();

    String getCommandUsage(ICommandSender var1);

    List getCommandAliases();

    void processCommand(ICommandSender var1, String[] var2);

    boolean canCommandSenderUseCommand(ICommandSender var1);

    List addTabCompletionOptions(ICommandSender var1, String[] var2);

    boolean isUsernameIndex(String[] var1, int var2);
}
