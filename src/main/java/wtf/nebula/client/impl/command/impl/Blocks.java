package wtf.nebula.client.impl.command.impl;

import net.minecraft.block.Block;
import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.command.arg.impl.BlockArgument;
import wtf.nebula.client.impl.module.visuals.XRay;

public class Blocks {

    public static class Xray extends Command {

        public Xray() {
            super(new String[]{"xray", "xrayblocks"}, new BlockArgument("block"));
        }

        @Override
        public String dispatch(CommandContext ctx) {
            int blockId = (Integer) ctx.get("block").getValue();
            String blockName = Block.getBlockById(blockId).getLocalizedName();

            String result;
            if (XRay.BLOCKS.contains(blockId)) {
                XRay.BLOCKS.remove(blockId);
                result = "Removed " + EnumChatFormatting.YELLOW + blockName + EnumChatFormatting.GRAY + " from your xray blocks.";
            } else {
                XRay.BLOCKS.add(blockId);
                result = "Added " + EnumChatFormatting.YELLOW + blockName + EnumChatFormatting.GRAY + " to your xray blocks.";
            }

            return result;
        }
    }
}
