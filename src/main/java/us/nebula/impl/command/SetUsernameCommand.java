package us.nebula.impl.command;

import net.minecraft.util.Session;
import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.CommandManifest;
import us.nebula.api.manager.command.CommandResult;
import us.nebula.api.manager.command.argument.type.StringArgument;
import us.nebula.util.ChatUtil;

import java.util.regex.Pattern;

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
                .argument(new StringArgument("username",
                        StringArgument.minMax(1, 16),
                        StringArgument.matches(Pattern.compile("^[a-zA-Z0-9_]+$")))
                        .setRequired(false), (arg) ->
                {
                    MC.setSession(new Session(arg.getValue(), "", ""));
                    ChatUtil.send("Set username to %s", arg.getValue());
                    return CommandResult.SUCCESS;
                })
                .dispatchSingle(() ->
                {
                    ChatUtil.send("Your current username is %s", MC.getSession().getUsername());
                    return CommandResult.SUCCESS;
                });
    }
}
