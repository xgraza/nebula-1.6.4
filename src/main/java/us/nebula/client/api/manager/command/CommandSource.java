package us.nebula.client.api.manager.command;

import us.nebula.client.util.player.ChatUtil;

public final class CommandSource
{
    public static final int SUCCESS = 1;

    private final Command command;
    private final String rawInput;
    private final CommandManager manager;

    public CommandSource(final Command command, final String rawInput, final CommandManager manager)
    {
        this.command = command;
        this.rawInput = rawInput;
        this.manager = manager;
    }

    public int respond()
    {
        return respond(null);
    }

    public int respond(final String message, final Object... format)
    {
        if (message != null && !message.isEmpty())
        {
            ChatUtil.send(message, format);
        }
        return SUCCESS;
    }

    public Command getCommand()
    {
        return command;
    }

    public String getRawInput()
    {
        return rawInput;
    }

    public CommandManager getManager()
    {
        return manager;
    }
}
