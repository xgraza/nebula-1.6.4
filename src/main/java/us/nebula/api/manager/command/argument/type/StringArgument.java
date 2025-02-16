package us.nebula.api.manager.command.argument.type;

import us.nebula.api.manager.command.argument.Argument;
import us.nebula.api.manager.command.exception.ArgumentResolveException;

/**
 * @author xgraza
 * @since 4.0.0
 */
public final class StringArgument extends Argument<String>
{
    public StringArgument(final String name)
    {
        super(String.class, name);
    }

    @Override
    public void resolve(String input) throws ArgumentResolveException
    {
        setValue(input);
    }
}
