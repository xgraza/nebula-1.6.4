package wtf.nebula.impl.command.impl;

import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.nebula.impl.command.Command;

import java.util.Arrays;
import java.util.List;

// thanks to medmex's Yeezus
public class Spawn extends Command {
    public Spawn() {
        super(Arrays.asList("spawn", "spawntp"), "Bukkit exploit");
    }

    @Override
    public void execute(List<String> args) {
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Double.NaN, Double.NaN, Double.NaN, Double.NaN, false));
        sendChatMessage("Packet sent");
    }
}
