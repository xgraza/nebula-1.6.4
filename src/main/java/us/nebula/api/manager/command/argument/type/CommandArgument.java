package us.nebula.api.manager.command.argument.type;

import us.nebula.Nebula;
import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.argument.Argument;
import us.nebula.api.manager.command.exception.ArgumentResolveException;

/**
 * @author xgraza
 * @since 4.0.0
 */
@SuppressWarnings("unchecked")
public final class CommandArgument extends Argument<Command>
{
    public CommandArgument(final String name)
    {
        super(Command.class, name);
    }

    @Override
    public void resolve(final String raw) throws ArgumentResolveException
    {
        final Command command = Nebula.INSTANCE.getCommandManager()
                .getReference(raw);
        if (command == null)
        {
            throw new ArgumentResolveException(this, raw);
        }
        setValue(command);
    }
}
