package wtf.nebula.impl.command.impl;

import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.Packet3Chat;
import wtf.nebula.impl.command.Command;
import wtf.nebula.repository.impl.FriendRepository;

import java.util.Arrays;
import java.util.List;

public class Friend extends Command {
    public Friend() {
        super(Arrays.asList("friend", "f", "fren"), "Adds / removes a friend");
    }


    @Override
    public void execute(List<String> args) {
        if (args.isEmpty()) {
            sendChatMessage("Please provide the friends name");
            return;
        }

        String username = args.get(0);
        if (FriendRepository.get().isFriend(username)) {
            FriendRepository.get().removeChild(username);
            sendChatMessage("Removed " + EnumChatFormatting.GREEN + username + EnumChatFormatting.RESET + " from your friends list");
        }

        else {
            FriendRepository.get().addChild(username);
            sendChatMessage("Added " + EnumChatFormatting.GREEN + username + EnumChatFormatting.RESET + " to your friends list");

            // we should notify this player they were added
            mc.thePlayer.sendQueue.addToSendQueue(new Packet3Chat("/msg " + username + " I just added you as a friend on Nebula!"));
        }
    }
}
