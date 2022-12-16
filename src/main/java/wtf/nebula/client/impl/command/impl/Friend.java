package wtf.nebula.client.impl.command.impl;

import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.command.arg.impl.StringArgument;

public class Friend extends Command {
    public Friend() {
        super(new String[]{"friend", "fren"},
                new StringArgument("action"),
                new StringArgument("playerName"));
    }

    @Override
    public String dispatch(CommandContext ctx) {
        String action = (String) ctx.get("action").getValue();
        String playerName = (String) ctx.get("playerName").getValue();

        if (action.equalsIgnoreCase("add") || action.equalsIgnoreCase("a")) {
            if (Nebula.getInstance().getFriendManager().isFriend(playerName)) {
                return "They are already added.";
            }

            Nebula.getInstance().getFriendManager().add(playerName);
            return "Added " + EnumChatFormatting.YELLOW + playerName + EnumChatFormatting.GRAY + " as a friend.";
        } else if (action.equalsIgnoreCase("remove") || action.equalsIgnoreCase("rm") || action.equalsIgnoreCase("del") || action.equalsIgnoreCase("delete")) {
            if (!Nebula.getInstance().getFriendManager().isFriend(playerName)) {
                return "They are not already friended.";
            }

            Nebula.getInstance().getFriendManager().remove(playerName);
            return "Removed " + EnumChatFormatting.YELLOW + playerName + EnumChatFormatting.GRAY + " as a friend.";
        }
        return EnumChatFormatting.YELLOW + "[add|a|remove|rm|del|delete] [playerName]" + EnumChatFormatting.GRAY + ".";
    }
}
