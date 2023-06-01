package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.listener.events.entity.move.EventMove;
import lol.nebula.listener.events.world.EventCollisionBox;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.player.MoveUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author aesthetical
 * @since 05/31/23
 */
public class AntiWeb extends Module {

    private static final AxisAlignedBB FULL_AABB = new AxisAlignedBB(
            0, 0, 0, 1, 1, 1);

    private final Setting<Mode> mode = new Setting<>(Mode.IGNORE, "Mode");
    private final Setting<Float> timer = new Setting<>(
            () -> mode.getValue() == Mode.TIMER, 1.5f, 0.01f, 1.0f, 20.0f, "Timer");
    private final Setting<Double> speed = new Setting<>(
            () -> mode.getValue() == Mode.MOTION, 0.25, 0.01, 0.1, 1.0, "Speed");

    private boolean usingTimer;

    public AntiWeb() {
        super("Anti Web", "Prevents you from being slowed in a web", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (usingTimer) {
            usingTimer = false;
            mc.timer.timerSpeed = 1.0f;
        }
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer.isInWeb) {

            if (mode.getValue() == Mode.IGNORE) {
                mc.thePlayer.isInWeb = false;
            } else if (mode.getValue() == Mode.TIMER) {
                usingTimer = true;
                mc.timer.timerSpeed = timer.getValue();
            }
        } else {

            if (usingTimer) {
                usingTimer = false;
                mc.timer.timerSpeed = 1.0f;
            }
        }
    }

    @Listener
    public void onMove(EventMove event) {
        if (mode.getValue() == Mode.MOTION && mc.thePlayer.isInWeb) {
            if (MoveUtils.isMoving()) {
                // hAllowedDistance = 0.4751131221719457 * thisMove.walkSpeed / 100D;
                MoveUtils.strafe(event, 0.4751131221719457 * (speed.getValue() * 100) / 100.0);
            }
        }
    }

    @Listener
    public void onCollisionBox(EventCollisionBox event) {
        if (mode.getValue() != Mode.BOUNDING_BOX) return;

        if (event.getEntity() == null
                || !event.getEntity().equals(mc.thePlayer)
                || !event.getBlock().equals(Blocks.web)) return;

        event.setAabb(FULL_AABB.copy().offset(event.getX(), event.getY(), event.getZ()));
    }

    public enum Mode {
        IGNORE, MOTION, TIMER, BOUNDING_BOX
    }
}
