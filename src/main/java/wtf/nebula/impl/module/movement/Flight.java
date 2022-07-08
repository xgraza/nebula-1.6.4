package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Packet10Flying;
import wtf.nebula.event.AddBoundingBoxEvent;
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
        // event.setY(mc.thePlayer.motionY = 0.0);
        event.setZ(motion[1]);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet10Flying && event.getEra().equals(Era.PRE)) {

            ((Packet10Flying) event.getPacket()).onGround = groundSpoof.getValue();
        }
    }

    @EventListener
    public void onCollisionBox(AddBoundingBoxEvent event) {
        if (mc.thePlayer.equals(event.getEntity())) {
            int x = event.getX();
            int y = (int) mc.thePlayer.boundingBox.minY - 1;
            int z = event.getZ();

            event.setBox(new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1));
            event.setCancelled(true);
        }
    }
}
