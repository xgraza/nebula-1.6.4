package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.listener.events.entity.move.EventStep;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

import java.util.HashMap;
import java.util.Map;

/**
 * @author aesthetical
 * @since 05/30/23
 */
public class Step extends Module {

    private static final Map<Double, double[]> NCP_STEP_POSITIONS = new HashMap<>();

    private final Setting<Mode> mode = new Setting<>(Mode.NCP, "Mode");
    private final Setting<Float> stepHeight = new Setting<>(1.0f, 0.1f, 0.75f, 2.5f, "Height");
    private final Setting<Boolean> useTimer = new Setting<>(
            () -> mode.getValue() == Mode.NCP, false, "Use Timer");

    private boolean usingTimer;

    public Step() {
        super("Step", "Lets you step up blocks faster", ModuleCategory.MOVEMENT);
    }

    @Override
    public String getMetadata() {
        return Setting.formatEnumName(mode.getValue()) + " " + stepHeight.getValue();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (usingTimer) mc.timer.timerSpeed = 1.0f;
        usingTimer = false;

        if (mc.thePlayer != null) mc.thePlayer.stepHeight = 0.6f;
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        mc.thePlayer.stepHeight = stepHeight.getValue();

        if (mode.getValue() == Mode.NCP && usingTimer && mc.thePlayer.onGround) {
            usingTimer = false;
            mc.timer.timerSpeed = 1.0f;
        }
    }

    @Listener
    public void onStep(EventStep event) {

        if ((event.getEntity() == null || !event.getEntity().equals(mc.thePlayer))) return;

        if (!mc.thePlayer.onGround) return;

        double height = mc.thePlayer.boundingBox.minY - (mc.thePlayer.posY - mc.thePlayer.yOffset);
        if (height > stepHeight.getValue() || height <= 0.0) return;

        double[] positions = NCP_STEP_POSITIONS.getOrDefault(height, NCP_STEP_POSITIONS.get(1.0));
        if (positions == null || positions.length == 0) return;

        if (useTimer.getValue()) {
            usingTimer = true;
            mc.timer.timerSpeed = 1.0f / (positions.length + 1);
        }

        double minY = mc.thePlayer.boundingBox.minY - 1.0;
        double stance = minY + (double) mc.thePlayer.yOffset - mc.thePlayer.ySize;

        for (double offset : positions) {
            mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(
                    mc.thePlayer.posX, minY + offset, stance + offset, mc.thePlayer.posZ, false));
        }
    }

    public enum Mode {
        NCP, VANILLA
    }

    static {
        NCP_STEP_POSITIONS.put(0.75, new double[] { .39, .7 });
        NCP_STEP_POSITIONS.put(0.8125, new double[] { .39, .7, .8125 });
        NCP_STEP_POSITIONS.put(0.875, new double[] { .39, .7, .875 });
        NCP_STEP_POSITIONS.put(1.0, new double[] { .42, .753 });
        NCP_STEP_POSITIONS.put(1.5, new double[] { .42, .75, 1.0, 1.16, 1.23, 1.2 });
        NCP_STEP_POSITIONS.put(2.0, new double[] { .42, .78, .51, .9, 1.21, 1.45, 1.43 });
        NCP_STEP_POSITIONS.put(2.5, new double[] { .425, .821, .699, .599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907 });
    }
}
