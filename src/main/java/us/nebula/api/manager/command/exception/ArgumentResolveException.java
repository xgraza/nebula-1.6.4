package us.nebula.api.manager.command.exception;

import us.nebula.api.manager.command.argument.Argument;

/**
 * @author xgraza
 * @since 4.0.0
 */
public final class ArgumentResolveException extends Exception
{
    private final Argument<?> argument;
    private final String message;

    public ArgumentResolveException(Argument<?> argument, String message)
    {
        this.argument = argument;
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message + " [Arg: " + argument.getName() + "]";
    }
}
