package us.nebula.api.manager.command.exception;

public final class CommandParseException extends Exception
{
    private final String message;

    public CommandParseException(String message)
    {
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }
}
