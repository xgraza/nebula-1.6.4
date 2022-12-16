package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.move.EventMotion;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.player.MoveUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestFly extends ToggleableModule {
    private final Property<Double> speed = new Property<>(0.1, 0.1, 5.0, "Speed", "s");
    private final Property<Boolean> antiKick = new Property<>(true, "Anti Kick", "antikick");

    private final List<Vec3> invalids = new CopyOnWriteArrayList<>();

    public TestFly() {
        super("Test Fly", new String[]{"testfly"}, ModuleCategory.MOVEMENT);
        offerProperties(speed, antiKick);
    }

    @Override
    public String getTag() {
        return String.valueOf(invalids.size());
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        invalids.clear();
        mc.thePlayer.noClip = false;
    }

    @EventListener
    public void onMotionUpdate(EventMotionUpdate event) {
        event.setCancelled(true);
    }

    @EventListener
    public void onMotion(EventMotion event) {
        double[] strafe = {0.0, 0.0};
        if (MoveUtils.isMoving()) {
            strafe = MoveUtils.calcStrafe(speed.getValue());
        }

        double motionY = 0.0;

        if (mc.gameSettings.keyBindJump.pressed) {
            motionY = 0.062;
        } else if (mc.gameSettings.keyBindSneak.pressed) {
            motionY = -0.062;
        } else {
            if (antiKick.getValue() && !MoveUtils.isMoving() && mc.thePlayer.ticksExisted % 40 == 0) {
                motionY = -0.04;
            }
        }

        mc.thePlayer.motionX = strafe[0];
        mc.thePlayer.motionZ = strafe[1];
        mc.thePlayer.motionY = motionY;

        Vec3 invalidVec = new Vec3(Vec3.fakePool,
                mc.thePlayer.posX + strafe[0],
                mc.thePlayer.posY + motionY,
                mc.thePlayer.posZ + strafe[1]);
        invalids.add(invalidVec);

        mc.thePlayer.sendQueue.addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(
                invalidVec.xCoord,
                invalidVec.yCoord,
                mc.thePlayer.posY + motionY,
                invalidVec.zCoord,
                false));

        mc.thePlayer.sendQueue.addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX,
                0,
                0,
                mc.thePlayer.posZ,
                false
        ));

        event.x = strafe[0];
        event.y = motionY;
        event.z = strafe[1];

        //mc.thePlayer.noClip = true;
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && event.getEra().equals(Era.PRE)) {
            S08PacketPlayerPosLook packet = event.getPacket();

            for (Vec3 vec3 : invalids) {

                if (vec3.xCoord == packet.x && vec3.yCoord == packet.y && vec3.zCoord == packet.z) {
                    invalids.remove(vec3);
                    print("lol");
                    event.setCancelled(true);
//                    mc.thePlayer.sendQueue.addToSendQueueSilent(new C03PacketPlayer.C06PacketPlayerPosLook(
//                            packet.x,
//                            packet.y - 1.62,
//                            packet.y,
//                            packet.z,
//                            packet.yaw,
//                            packet.pitch,
//                            false
//                    ));

                    break;
                }
            }
        }
    }
}
