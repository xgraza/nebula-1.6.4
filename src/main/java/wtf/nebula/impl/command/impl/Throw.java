package wtf.nebula.impl.command.impl;

import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet15Place;
import wtf.nebula.impl.command.Command;

import java.util.Collections;
import java.util.List;

public class Throw extends Command {
    public Throw() {
        super(Collections.singletonList("throw"), "Throws down an entire stack at once");
    }

    @Override
    public void execute(List<String> args) {
        ItemStack held = mc.thePlayer.getHeldItem();
        if (held == null) {
            sendChatMessage("You are not holding an item");
            return;
        }

        int amount = held.stackSize;
        if (!args.isEmpty()) {
            try {
                amount = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ignored) {

            }

            amount = Math.min(amount, held.getMaxStackSize());
        }

        for (int i = 0; i < amount; ++i) {
            mc.thePlayer.sendQueue.addToSendQueue(new Packet15Place(-1, -1, -1, 255, held, 0.0f, 0.0f, 0.0f));
        }

        sendChatMessage("Sent " + EnumChatFormatting.GREEN + amount + EnumChatFormatting.RESET + " packet(s)");
    }
}
