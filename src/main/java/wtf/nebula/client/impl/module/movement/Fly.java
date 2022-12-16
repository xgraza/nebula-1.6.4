package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.world.WorldSettings;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.player.MoveUtils;

import java.util.ArrayList;
import java.util.List;

public class Fly extends ToggleableModule {
    private final Property<Mode> mode = new Property<>(Mode.VANILLA, "Mode", "m", "type");
    private final Property<Double> speed = new Property<>(1.0, 0.1, 10.0, "Speed");

    public Fly() {
        super("Fly", new String[]{"flight"}, ModuleCategory.MOVEMENT);
        offerProperties(mode, speed);
    }

    @Override
    public String getTag() {
        return mode.getFixedValue();
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        if (!mc.playerController.currentGameType.equals(WorldSettings.GameType.CREATIVE) && !isNull()) {
            mc.thePlayer.capabilities.isFlying = false;
            mc.thePlayer.capabilities.allowFlying = true;
        }

    }

    @EventListener
    public void onTick(EventTick event) {
        if (mode.getValue().equals(Mode.VANILLA)) {
            mc.thePlayer.motionY = 0.0;

            if (MoveUtils.isMoving()) {
                double[] strafe = MoveUtils.calcStrafe(speed.getValue());

                mc.thePlayer.motionX = strafe[0];
                mc.thePlayer.motionZ = strafe[1];
            } else {
                mc.thePlayer.motionX = 0.0;
                mc.thePlayer.motionZ = 0.0;
            }

            if (mc.gameSettings.keyBindJump.pressed) {
                mc.thePlayer.motionY = speed.getValue();
            } else if (mc.gameSettings.keyBindSneak.pressed) {
                mc.thePlayer.motionY = -speed.getValue();
            }
        } else if (mode.getValue().equals(Mode.CREATIVE)) {
            mc.thePlayer.capabilities.isFlying = true;
            mc.thePlayer.capabilities.allowFlying = true;
            mc.thePlayer.capabilities.setFlySpeed(0.05f * speed.getValue().floatValue());
        }
    }

    public enum Mode {
        VANILLA, CREATIVE
    }
}
