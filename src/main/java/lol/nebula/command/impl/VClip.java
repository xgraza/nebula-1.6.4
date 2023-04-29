package lol.nebula.command.impl;

import lol.nebula.command.Command;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class VClip extends Command {
    public VClip() {
        super(new String[]{"vclip", "verticalclip"}, "Clips your player vertically", "<blocks>");
    }

    @Override
    public String dispatch(String[] args) {
        if (args.length == 0) return getSyntax();

        double value;
        try {
            value = Double.parseDouble(args[0]);
        } catch (NumberFormatException ignored) {
            return "You must provide a valid number";
        }

        // update the player's position
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.boundingBox.minY + value, mc.thePlayer.posZ);

        return "Clipped " + String.format("%.1f", value) + " blocks";
    }
}
