package wtf.nebula.client.impl.command.impl;

import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.CommandContext;

public class SpawnTP extends Command {
    public SpawnTP() {
        super(new String[]{"spawntp", "spawn", "spawnteleport"});
    }

    @Override
    public String dispatch(CommandContext ctx) {
        mc.thePlayer.sendQueue.addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(
                Double.NaN,
                Double.NaN,
                Double.NaN,
                Double.NaN,
                false
        ));

        return "Sent invalid packet";
    }
}
