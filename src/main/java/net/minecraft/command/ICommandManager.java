package net.minecraft.command;

import java.util.List;
import java.util.Map;

public interface ICommandManager
{
    int executeCommand(ICommandSender var1, String var2);

    List getPossibleCommands(ICommandSender var1, String var2);

    List getPossibleCommands(ICommandSender var1);

    Map getCommands();
}
