package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet10Flying;
import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.Packet15Place;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.MotionUpdateEvent;
import wtf.nebula.event.MotionUpdateEvent.Era;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PlayerSlowdownEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class NoSlow extends Module {
    public NoSlow() {
        super("NoSlow", ModuleCategory.MOVEMENT);
    }

    public final Value<Boolean> ncp = new Value<>("NCP", true);

    @EventListener
    public void onPlayerSlowdown(PlayerSlowdownEvent event) {
        if (mc.thePlayer.isUsingItem() && !mc.thePlayer.isRiding()) {

            event.getMovementInput().moveForward *= 5.0f;
            event.getMovementInput().moveStrafe *= 5.0f;
        }
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (mc.thePlayer.isBlocking() && ncp.getValue()) {
            if (event.getEra().equals(Era.PRE)) {
                mc.thePlayer.sendQueue.addToSendQueue(new Packet14BlockDig(5, 0, 0, 0, 255));
            }

            else {
                mc.thePlayer.sendQueue.addToSendQueue(new Packet15Place(-1, -1, -1, 255, mc.thePlayer.getHeldItem(), 0.0F, 0.0F, 0.0F));
            }
        }
    }
}
