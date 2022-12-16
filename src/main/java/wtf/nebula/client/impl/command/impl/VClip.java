package wtf.nebula.client.impl.command.impl;

import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.Argument;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.command.arg.impl.NumberArgument;

public class VClip extends Command {
    public VClip() {
        super(new String[]{"vclip", "veritcal", "up", "down"},
                new NumberArgument<Double>("blocks"));
    }

    @Override
    public String dispatch(CommandContext ctx) {
        Number dist = (Number) ctx.get("blocks").getValue();
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + dist.doubleValue(), mc.thePlayer.posZ);
        return "Clipped " + EnumChatFormatting.YELLOW + dist + EnumChatFormatting.GRAY + " blocks.";
    }
}
