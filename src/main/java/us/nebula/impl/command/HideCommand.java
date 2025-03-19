package us.nebula.impl.command;

import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.CommandManifest;
import us.nebula.api.manager.command.CommandResult;
import us.nebula.api.manager.command.argument.type.BooleanArgument;
import us.nebula.api.manager.command.argument.type.CheatArgument;
import us.nebula.util.player.ChatUtil;

/**
 * @author xgraza
 * @since 03/19/25
 */
@CommandManifest(aliases = {"hide", "drawn"},
        description = "Hides or unhinds a module from the renderable array list")
public final class HideCommand extends Command
{
    @Override
    public void build()
    {
        argumentBuilder
                .argument(new CheatArgument("cheat"), (arg) ->
                {
                    // TODO: better api usage bc wtf is this
                    Boolean hiddenState = argumentBuilder.<Boolean>getArgument("state").getValue();
                    if (hiddenState == null)
                    {
                        hiddenState = !arg.getValue().isHidden();
                    }
                    arg.getValue().setHidden(hiddenState);
                    ChatUtil.send("%s is now %s from the renderable array list.",
                            arg.getValue().getManifest().name(),
                            hiddenState ? "hidden" : "not hidden");
                    return CommandResult.SUCCESS;
                })
                .argument(new BooleanArgument("state")
                        .setRequired(false));
    }
}
