package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.timing.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import java.util.List;

/**
 * @author aesthetical
 * @since 04/29/23
 *
 * this is a joke module i hate rapists
 */
public class AutoRape extends Module {
    private static final double OFFSET = 0.05;

    private final Setting<Integer> strokes = new Setting<>(10, 1, 60, "Strokes");
    private final Setting<Float> difference = new Setting<>(40.0f, 0.01f, 1.0f, 90.0f, "Difference");

    private final Timer timer = new Timer();
    private boolean override;

    public AutoRape() {
        super("Auto Rape", "Automatically does unspeakable things to the person in front of you", ModuleCategory.PLAYER);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        // reset sneak state
        if (mc.gameSettings != null && override) mc.gameSettings.keyBindSneak.pressed = false;
        override = false;
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        List<EntityPlayer> entities = mc.theWorld.playerEntities;

        // if there are no entities in the world, don't do anything
        if (entities.isEmpty()) return;

        // our target
        EntityPlayer player = null;
        for (EntityPlayer entity : entities) {

            // if the entity is null/dead (no hitbox) or is ourselves, continue
            if (entity == null || entity.isDead || entity.equals(mc.thePlayer)) continue;

            // if we are intersecting with their hitbox, we have a target
            if (entity.boundingBox.copy().expand(OFFSET, OFFSET, OFFSET).intersectsWith(mc.thePlayer.boundingBox)) {
                player = entity;
                break;
            }
        }

        // if we don't have a target, rip
        if (player == null) {
            if (override) mc.gameSettings.keyBindSneak.pressed = false;
            override = false;
            return;
        }

        // get the difference between our yaw and their body rotation
        float diff = MathHelper.wrapAngleTo180_float(player.renderYawOffset)
                - MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);

        // if their turned too far towards us, stop
        if (diff > difference.getValue() || diff < -difference.getValue()) {
            if (override) mc.gameSettings.keyBindSneak.pressed = false;
            override = false;
            return;
        }

        // every x ms (so strokes per second)
        if (timer.ms(1000L / strokes.getValue(), true)) {
            override = true;
            mc.gameSettings.keyBindSneak.pressed = !mc.gameSettings.keyBindSneak.pressed;
        }
    }
}
