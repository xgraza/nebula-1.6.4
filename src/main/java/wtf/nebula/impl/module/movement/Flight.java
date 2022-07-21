package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet10Flying;
import wtf.nebula.event.MotionEvent;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.world.player.MotionUtil;

public class Flight extends Module {
    public Flight() {
        super("Flight", ModuleCategory.MOVEMENT);
    }

    public final Value<Double> speed = new Value<>("Speed", 0.2, 0.1, 5.0);
    public final Value<Boolean> groundSpoof = new Value<>("GroundSpoof", true);

    @EventListener
    public void onMotion(MotionEvent event) {
        double[] motion = MotionUtil.strafe(speed.getValue());

        event.setX(motion[0]);
        event.setZ(motion[1]);

        double motionY = 0.0;

        if (mc.gameSettings.keyBindJump.pressed) {
            motionY = speed.getValue();
        }

        else if (mc.gameSettings.keyBindSneak.pressed) {
            motionY = -speed.getValue();
        }

        event.setY(mc.thePlayer.motionY = motionY);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet10Flying && event.getEra().equals(Era.PRE)) {

            ((Packet10Flying) event.getPacket()).onGround = groundSpoof.getValue();
        }
    }
}
