package us.nebula.impl.command;

import net.minecraft.util.Session;
import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.CommandManifest;
import us.nebula.api.manager.command.CommandResult;
import us.nebula.api.manager.command.argument.type.Arguments;
import us.nebula.util.ChatUtil;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CommandManifest(aliases = {"setuser", "setusername", "username"})
public final class SetUsernameCommand extends Command
{
    @Override
    public void build()
    {
        argumentBuilder
                .argument(Arguments.string("username")
                        .setRequired(false), (arg) ->
                {
                    MC.setSession(new Session(arg.getValue(), "", ""));
                    ChatUtil.send("Set username to {}", arg.getValue());
                    return CommandResult.SUCCESS;
                })
                .dispatchSingle(() ->
                {
                    ChatUtil.send("Your current username is {}", MC.getSession().getUsername());
                    return CommandResult.SUCCESS;
                });
    }
}
