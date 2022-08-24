package wtf.nebula.impl.command.impl;

import net.minecraft.block.Block;
import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.impl.command.Command;
import wtf.nebula.impl.module.render.XRay;
import wtf.nebula.repository.impl.ModuleRepository;

import java.util.Arrays;
import java.util.List;

public class Xray extends Command {
    public Xray() {
        super(Arrays.asList("xray", "xrayblock", "xrayblocks", "wallhack"), "Adds/removes/lists xray blocks");
    }

    @Override
    public void execute(List<String> args) {
        if (args.isEmpty()) {

            StringBuilder builder = new StringBuilder("All xray blocks")
                    .append(" ")
                    .append(EnumChatFormatting.GREEN)
                    .append("(")
                    .append(XRay.blocks.size())
                    .append("):")
                    .append(EnumChatFormatting.RESET)
                    .append("\n");

            for (int blockId : XRay.blocks) {
                try {
                    Block block = Block.getBlockById(blockId);
                    if (block != null) {
                        builder.append(block.getLocalizedName())
                                .append("[")
                                .append(blockId)
                                .append("]")
                                .append(" ");
                    }
                } catch (IndexOutOfBoundsException ignored) {

                }
            }

            sendChatMessage(builder.toString());
        }

        else {

            int blockId = -1;

            try {
                blockId = Integer.parseInt(args.get(0));
            } catch (NumberFormatException e) {
                sendChatMessage("That is not a valid number");
                return;
            }

            if (blockId == -1) {
                sendChatMessage("That is not a valid number");
                return;
            }

            try {
                Block block = Block.getBlockById(blockId);
                if (block == null) {
                    sendChatMessage("That is not a valid block id");
                    return;
                }

                if (XRay.blocks.contains(blockId)) {
                    XRay.blocks.remove(blockId);
                    sendChatMessage("Removed block " + EnumChatFormatting.GREEN + block.getLocalizedName() + EnumChatFormatting.RESET + " [" + blockId + "]");
                }

                else {
                    XRay.blocks.add(blockId);
                    sendChatMessage("Added block " + EnumChatFormatting.GREEN + block.getLocalizedName() + EnumChatFormatting.RESET + " [" + blockId + "]");
                }

                if (ModuleRepository.get().getModule(XRay.class).getState() && !nullCheck()) {
                    mc.renderGlobal.loadRenderers();
                }
            } catch (IndexOutOfBoundsException e) {
                sendChatMessage("That is not a valid block id");
            }
        }
    }
}
