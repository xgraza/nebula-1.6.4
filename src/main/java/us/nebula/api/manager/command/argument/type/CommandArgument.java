package us.nebula.api.manager.command.argument.type;

import us.nebula.Nebula;
import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.argument.Argument;
import us.nebula.api.manager.command.exception.ArgumentResolveException;

/**
 * @author xgraza
 * @since 4.0.0
 */
public final class CommandArgument extends Argument<Command>
{
    public CommandArgument(final String name)
    {
        super(Command.class, name);
    }

    @Override
    public void resolve(final String input) throws ArgumentResolveException
    {
        final Command command = Nebula.INSTANCE.getCommandManager().getReference(input);
        if (command == null)
        {
            throw new ArgumentResolveException(this, input);
        }
        setValue(command);
    }
}
