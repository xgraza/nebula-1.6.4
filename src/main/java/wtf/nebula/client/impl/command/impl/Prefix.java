package wtf.nebula.client.impl.command.impl;

import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.command.arg.impl.StringArgument;

public class Prefix extends Command {
    public Prefix() {
        super(new String[]{"prefix", "commandprefix", "cmdprefix"},
                new StringArgument("prefix"));
    }

    @Override
    public String dispatch(CommandContext ctx) {
        String newPrefix = (String) ctx.get("prefix").getValue();
        Nebula.getInstance().getCommandManager().commandPrefix = newPrefix;
        return "Set the prefix to " + EnumChatFormatting.YELLOW + newPrefix + EnumChatFormatting.GRAY + ".";
    }
}
