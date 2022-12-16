package wtf.nebula.client.impl.command.impl;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.command.arg.impl.NumberArgument;
import wtf.nebula.client.utils.player.PlayerUtils;

public class HClip extends Command {
    public HClip() {
        super(new String[]{"hclip", "horizontal", "through", "thru"},
                new NumberArgument<Double>("blocks"));
    }

    @Override
    public String dispatch(CommandContext ctx) {
        Number dist = (Number) ctx.get("blocks").getValue();
        EnumFacing facing = PlayerUtils.getFacing();
        mc.thePlayer.setPosition(
                mc.thePlayer.posX + (dist.doubleValue() * facing.getFrontOffsetX()),
                mc.thePlayer.posY,
                mc.thePlayer.posZ + (dist.doubleValue() * facing.getFrontOffsetZ()));
        return "Clipped through " + EnumChatFormatting.YELLOW + dist + EnumChatFormatting.GRAY + " blocks.";
    }
}
