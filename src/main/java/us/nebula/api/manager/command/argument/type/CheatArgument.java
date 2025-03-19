package us.nebula.api.manager.command.argument.type;

import us.nebula.Nebula;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.command.argument.Argument;
import us.nebula.api.manager.command.exception.ArgumentResolveException;

/**
 * @author xgraza
 * @since 03/19/25
 */
public final class CheatArgument extends Argument<Cheat>
{
    public CheatArgument(final String name)
    {
        super(Cheat.class, name);
    }

    @Override
    public void resolve(final String raw) throws ArgumentResolveException
    {
        final String parsed = raw.toLowerCase()
                .trim()
                .replaceAll(" ", "");
        for (final Cheat cheat : Nebula.INSTANCE.getCheatManager().getAll())
        {
            if (cheat.getManifest().name().equalsIgnoreCase(parsed))
            {
                setValue(cheat);
                return;
            }
        }
        throw new ArgumentResolveException(this, parsed);
    }
}
