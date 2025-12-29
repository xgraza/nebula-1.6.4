package us.nebula.api.manager.command.argument;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatManager;
import world.xgraza.xcmd.parser.argument.Argument;
import world.xgraza.xcmd.parser.argument.exception.ArgumentParseException;

import java.util.List;

/**
 * @author xgraza
 * @since 12/29/25
 */
public final class ArgumentCheat extends Argument<Cheat>
{
    private final CheatManager cheatManager;

    public ArgumentCheat(final CheatManager cheatManager, final String name)
    {
        super(Cheat.class, name);
        this.cheatManager = cheatManager;
    }

    @Override
    public Cheat parse(final String raw, final String type) throws ArgumentParseException
    {
        final List<Cheat> cheatList = cheatManager.getAll();
        for (final Cheat cheat : cheatList)
        {
            final String name = cheat.getManifest().name();
            if (name.equalsIgnoreCase(raw))
            {
                return cheat;
            }
        }
        return null;
    }

    public static ArgumentCheat cheat(final CheatManager cheatManager, final String name)
    {
        return new ArgumentCheat(cheatManager, name);
    }
}
