package us.nebula.impl.command;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatManager;
import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.CommandManifest;
import us.nebula.api.manager.command.argument.ArgumentCheat;
import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.parser.CommandContext;

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
}
