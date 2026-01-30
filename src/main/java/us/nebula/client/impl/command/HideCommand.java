package us.nebula.client.impl.command;

import us.nebula.client.Nebula;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatManager;
import us.nebula.client.api.manager.command.Command;
import us.nebula.client.api.manager.command.CommandManifest;
import us.nebula.client.api.manager.command.argument.ArgumentCheat;
import us.xgraza.xcmd.executor.CommandResult;
import us.xgraza.xcmd.parser.CommandContext;
import us.xgraza.xcmd.parser.argument.Argument;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 12/29/25
 */
@CommandManifest(aliases = { "hide", "show" },
        description = "Hides/shows cheats in the array list")
public final class HideCommand extends Command
{
    public HideCommand(final CheatManager cheatManager)
    {
        registerArgument(ArgumentCheat.cheat(cheatManager, "cheat"));
    }

    @Override
    public CommandResult dispatch(final CommandContext ctx)
    {
        final Cheat cheat = ctx.getArgument("cheat");
        cheat.setHidden(!cheat.isHidden());
        return ctx.ok("Cheat is now " + (cheat.isHidden() ? "hidden" : "shown"));
    }

    @Override
    public List<String> suggest(final Argument<?> argument, String input)
    {
        input = input.trim()
                .toLowerCase()
                .replaceAll(" ", "");
        final List<String> suggestions = new LinkedList<>();
        for (final Cheat cheat : Nebula.INSTANCE.getCheatManager().getAll())
        {
            final String name = cheat.getManifest().name().toLowerCase();
            if (input.equals(name) || name.contains(input))
            {
                suggestions.add(cheat.getManifest().name());
            }
        }
        return suggestions;
    }
}
