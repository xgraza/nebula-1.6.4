package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.move.EventStep;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

import java.util.HashMap;
import java.util.Map;

public class Step extends ToggleableModule {
    private static final Map<Double, double[]> NCP_STEP_HEIGHTS = new HashMap<>();

    private final Property<Mode> mode = new Property<>(Mode.NORMAL, "Mode", "m", "type");
    private final Property<Double> height = new Property<>(1.0, 0.7, 2.5, "Height", "blocks");
    private final Property<Boolean> useTimer = new Property<>(true, "Use Timer", "timer")
            .setVisibility(() -> mode.getValue().equals(Mode.NORMAL));

    private boolean timer = false;

    public Step() {
        super("Step", new String[]{}, ModuleCategory.MOVEMENT);
        offerProperties(mode, height, useTimer);

        NCP_STEP_HEIGHTS.put(.75, new double[] { .39, .7 });
        NCP_STEP_HEIGHTS.put(.8125, new double[] { .39, .7, .8125 });
        NCP_STEP_HEIGHTS.put(.875, new double[] { .39, .7, .875 });
        NCP_STEP_HEIGHTS.put(1.0, new double[] { .42, .753 });
        NCP_STEP_HEIGHTS.put(1.5, new double[] { .42, .75, 1.0, 1.16, 1.23, 1.2 });
        NCP_STEP_HEIGHTS.put(2.0, new double[] { .42, .78, .51, .9, 1.21, 1.45, 1.43 });
        NCP_STEP_HEIGHTS.put(2.5, new double[] { .425, .821, .699, .599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907 });
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        mc.timer.timerSpeed = 1.0f;
        mc.thePlayer.stepHeight = 0.5f;
        timer = false;
    }

    @Override
    public String getTag() {
        return mode.getFixedValue();
    }

    @EventListener
    public void onTick(EventTick event) {
        if (mc.thePlayer.onGround && timer) {
            timer = false;
            mc.timer.timerSpeed = 1.0f;
        }

        mc.thePlayer.stepHeight = height.getValue().floatValue();
    }

    @EventListener
    public void onStep(EventStep event) {
        if (mode.getValue().equals(Mode.NORMAL) && !isNull() && mc.thePlayer.onGround) {
            double stepHeight = mc.thePlayer.boundingBox.minY - (mc.thePlayer.posY - mc.thePlayer.yOffset);
            double[] h = NCP_STEP_HEIGHTS.getOrDefault(stepHeight, null);

            if (h == null || h.length == 0) {
                return;
            }

            if (useTimer.getValue()) {
                timer = true;
                mc.timer.timerSpeed = 1.0f / (h.length + 1.0f);
            }

            double minY = mc.thePlayer.boundingBox.minY - 1.0;
            double stance = minY + (double) mc.thePlayer.yOffset - mc.thePlayer.ySize;

            for (double i : h) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        minY + i,
                        stance + i,
                        mc.thePlayer.posZ,
                        false));
            }
        }
    }

    public enum Mode {
        NORMAL, VANILLA
    }
}
