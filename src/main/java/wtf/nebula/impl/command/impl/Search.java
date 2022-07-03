package wtf.nebula.impl.command.impl;

import net.minecraft.src.Block;
import net.minecraft.src.EnumChatFormatting;
import wtf.nebula.impl.command.Command;
import wtf.nebula.impl.module.render.Searcher;

import java.util.Arrays;
import java.util.List;

public class Search extends Command {
    public Search() {
        super(Arrays.asList("search", "searcher", "searchblocks"), "Manages search blocks");
    }

    @Override
    public void execute(List<String> args) {
        if (args.isEmpty()) {

            StringBuilder builder = new StringBuilder("All search blocks")
                    .append(" ")
                    .append(EnumChatFormatting.GREEN)
                    .append("(")
                    .append(Searcher.blocks.size())
                    .append("):")
                    .append(EnumChatFormatting.RESET)
                    .append("\n");

            for (int blockId : Searcher.blocks) {
                try {
                    Block block = Block.blocksList[blockId];
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
                Block block = Block.blocksList[blockId];
                if (block == null) {
                    sendChatMessage("That is not a valid block id");
                    return;
                }

                if (Searcher.blocks.contains(blockId)) {
                    Searcher.blocks.remove(blockId);
                    sendChatMessage("Removed block " + EnumChatFormatting.GREEN + block.getLocalizedName() + EnumChatFormatting.RESET + " [" + blockId + "]");
                }

                else {
                    Searcher.blocks.add(blockId);
                    sendChatMessage("Added block " + EnumChatFormatting.GREEN + block.getLocalizedName() + EnumChatFormatting.RESET + " [" + blockId + "]");
                }
            } catch (IndexOutOfBoundsException e) {
                sendChatMessage("That is not a valid block id");
            }
        }
    }
}
