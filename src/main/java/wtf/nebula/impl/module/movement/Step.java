package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet11PlayerPosition;
import wtf.nebula.event.StepEvent;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

import java.util.HashMap;
import java.util.Map;

public class Step extends Module {
    private static final Map<Double, double[]> NCP = new HashMap<Double, double[]>() {{
        put(.75, new double[] { .42, .75, .654 });
        put(.8125, new double[] { .42, .75, .654 });
        put(.875, new double[] { .39, .7, .875 });
        put(1.0, new double[] { .42, .75 });
        put(1.5, new double[] { .42, .75, 1.0, 1.16, 1.23, 1.2 });
        put(2.0, new double[] { .42, .78, .51, .9, 1.21, 1.45, 1.43 });
        put(2.5, new double[] { .425, .821, .699, .599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907 });
    }};

    public Step() {
        super("Step", ModuleCategory.MOVEMENT);
    }

    public final Value<Double> height = new Value<>("Height", 1.0, 1.0, 2.5);
    public final Value<Boolean> vanilla = new Value<>("Vanilla", false);

    private boolean timer = false;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        mc.thePlayer.stepHeight = 0.6f;
        mc.timer.timerSpeed = 1.0f;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (timer && mc.thePlayer.onGround) {
            mc.timer.timerSpeed = 1.0f;
        }

        mc.thePlayer.stepHeight = height.getValue().floatValue();
    }

    @EventListener
    public void onStep(StepEvent event) {
        if (vanilla.getValue() || nullCheck()) {
            return;
        }

        double stepHeight = mc.thePlayer.boundingBox.minY - (mc.thePlayer.posY - mc.thePlayer.yOffset);

        if (stepHeight == 0.0 || stepHeight > height.getValue()) {
            return;
        }

        double[] positions = NCP.getOrDefault(stepHeight, null);
        if (positions == null || positions.length == 0) {
            return;
        }

        timer = true;
        mc.timer.timerSpeed = 1.0f / (positions.length + 1.0f);

        for (double i : positions) {

            double minY = event.getBox().minY - 1.0;
            //double stance = minY + 1.620;
            double stance = minY + (double) mc.thePlayer.yOffset - mc.thePlayer.ySize;

            Packet11PlayerPosition packet = new Packet11PlayerPosition(
                    mc.thePlayer.posX,
                    minY + i,
                    stance + i,
                    mc.thePlayer.posZ,
                    false);

            // this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
            mc.thePlayer.sendQueue.addToSendQueueSilent(packet);

            sendChatMessage("Y: " + packet.yPosition + ", Stance: " + packet.stance);
        }
    }
}
