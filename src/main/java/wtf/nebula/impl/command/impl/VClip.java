package wtf.nebula.impl.command.impl;

import wtf.nebula.impl.command.Command;

import java.util.Arrays;
import java.util.List;

public class VClip extends Command {
    public VClip() {
        super(Arrays.asList("vclip", "vcl"), "Vertical clips you");
    }

    @Override
    public void execute(List<String> args) {
        int blocks = 0;
        try {
            blocks = Integer.parseInt(args.get(0));
        } catch (Exception e) {
            sendChatMessage("Please provide a proper number");
            return;
        }

        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.boundingBox.minY + blocks, mc.thePlayer.posZ);
    }
}
