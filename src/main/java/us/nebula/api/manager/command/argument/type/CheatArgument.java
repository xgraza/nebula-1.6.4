package us.nebula.api.manager.command.argument.type;

import us.nebula.Nebula;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.command.argument.Argument;
import us.nebula.api.manager.command.exception.ArgumentResolveException;

import java.util.LinkedList;
import java.util.List;

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

    @Override
    public List<String> computeSuggestions(String input)
    {
        input = input.trim().replaceAll(" ", "").toLowerCase();
        final List<String> suggestionList = new LinkedList<>();
        for (final Cheat cheat : Nebula.INSTANCE.getCheatManager().getAll())
        {
            final String name = cheat.getManifest().name();
            if (name.contains(input))
            {
                suggestionList.add(name);
            }
        }
        return suggestionList;
    }
}
