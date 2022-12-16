package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.client.MathUtils;

public class AntiAim extends ToggleableModule {
    private final Property<Pitch> pitch = new Property<>(Pitch.OFF, "Pitch", "p");
    private final Property<Yaw> yaw = new Property<>(Yaw.SPIN, "Yaw", "y");
    private final Property<Boolean> server = new Property<>(true, "Server", "silent");
    private final Property<Float> speed = new Property<>(2.5f, 0.1f, 50.0f, "Speed", "s");

    public AntiAim() {
        super("Anti Aim", new String[]{"antiaim", "noaim", "derp", "retard"}, ModuleCategory.MISCELLANEOUS);
        offerProperties(pitch, yaw, server, speed);
    }

    @EventListener
    public void onMotionUpdate(EventMotionUpdate event) {
        float[] rotations = Nebula.getInstance().getRotationManager().getServerRotation();
        if (!server.getValue()) {
            rotations = new float[] { mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch };
        }

        switch (pitch.getValue()) {
            case OFF: {
                rotations[1] = mc.thePlayer.rotationPitch;
                break;
            }

            case INVALID: {
                rotations[1] += speed.getValue();
                break;
            }

            case ZERO: {
                rotations[1] = 0.0f;
                break;
            }

            case SKY: {
                rotations[1] = -90.0f;
                break;
            }

            case RANDOM: {
                float rand = MathUtils.random(-90, 90);
                rotations[1] = rand;
                break;
            }
        }

        switch (yaw.getValue()) {
            case OFF: {
                rotations[0] = mc.thePlayer.rotationYaw;
                break;
            }

            case SPIN: {
                rotations[0] += speed.getValue();
                break;
            }

            case RANDOM: {
                float rand = MathUtils.random(0, 360);
                rotations[0] = rand;
                break;
            }
        }

        if (server.getValue()) {
            Nebula.getInstance().getRotationManager().setRotations(rotations);
        } else {
            mc.thePlayer.rotationYaw = rotations[0];
            mc.thePlayer.rotationYawHead = rotations[0];
            mc.thePlayer.rotationPitch = rotations[1];
        }
    }

    public enum Pitch {
        OFF, ZERO, SKY, RANDOM, INVALID
    }

    public enum Yaw {
        OFF, SPIN, RANDOM
    }
}
