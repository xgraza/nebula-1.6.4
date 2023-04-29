package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;

import static java.lang.String.format;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class AutoRespawn extends Module {
    private final Setting<Boolean> deathCoords = new Setting<>(true, "Death Coords");

    public AutoRespawn() {
        super("Auto Respawn", "Automatically respawns when you die", ModuleCategory.PLAYER);
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0.0f) {
            mc.thePlayer.respawnPlayer();
        }
    }

    @Listener
    public void onPacketOutbound(EventPacket.Outbound event) {
        if (event.getPacket() instanceof C16PacketClientStatus) {
            C16PacketClientStatus packet = event.getPacket();

            if (packet.func_149435_c() != EnumState.PERFORM_RESPAWN) return;

            if (deathCoords.getValue()) {
                print(format("You died at XYZ: %.1f, %.1f, %.1f",
                        mc.thePlayer.posX, mc.thePlayer.boundingBox.minY, mc.thePlayer.posZ));
            }
        }
    }
}
