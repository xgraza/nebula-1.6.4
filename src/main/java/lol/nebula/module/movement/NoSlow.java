package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.EventStage;
import lol.nebula.listener.events.entity.move.EventSlowdown;
import lol.nebula.listener.events.entity.move.EventWalkingUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class NoSlow extends Module {
    public NoSlow() {
        super("No Slow", "Stops items from slowing you down", ModuleCategory.MOVEMENT);
    }

    @Listener
    public void onSlowdown(EventSlowdown event) {
        if (!mc.thePlayer.isRiding() && mc.thePlayer.isUsingItem()) {
            event.getInput().moveForward *= 5.0f;
            event.getInput().moveStrafe *= 5.0f;
        }
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {
        if (!mc.thePlayer.isRiding() && mc.thePlayer.isBlocking()) {
            if (event.getStage() == EventStage.PRE) {
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                        5, 0, 0, 0, 255));
            } else {
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                        -1, -1, -1, 255, mc.thePlayer.getHeldItem(), 0.0F, 0.0F, 0.0F));
            }
        }
    }
}
