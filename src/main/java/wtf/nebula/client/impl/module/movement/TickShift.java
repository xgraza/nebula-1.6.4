package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.MathHelper;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.player.MoveUtils;

public class TickShift extends ToggleableModule {

    private final Property<Integer> ticks = new Property<>(20, 10, 120, "Ticks", "t");
    private final Property<Float> boost = new Property<>(1.0f, 0.1f, 5.0f, "Boost", "speed");
    private final Property<Boolean> autoDisable = new Property<>(true, "Auto Disable", "autodisable");

    private int tickCount = 0;

    public TickShift() {
        super("Tick Shift", new String[]{"tickshift", "warp"}, ModuleCategory.MOVEMENT);
        offerProperties(ticks, boost, autoDisable);
    }

    @Override
    public String getTag() {
        return String.valueOf(tickCount);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        tickCount = 0;
        if (!isNull()) {
            mc.timer.timerSpeed = 1.0f;
        }
    }

    @EventListener
    public void onTick(EventTick event) {
        if (!MoveUtils.isMoving()) {
            ++tickCount;
            mc.timer.timerSpeed = 1.0f;
        } else {
            --tickCount;
            if (tickCount >= 0) {
                mc.timer.timerSpeed = 1.0f + boost.getValue();
            } else {
                mc.timer.timerSpeed = 1.0f;
                if (autoDisable.getValue()) {
                    setRunning(false);
                }
            }
        }

        tickCount = MathHelper.clamp_int(tickCount, 0, ticks.getValue());
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (isNull()) {
            return;
        }

        if (event.getPacket() instanceof S08PacketPlayerPosLook) {

            if (autoDisable.getValue()) {
                setRunning(false);
            } else {
                tickCount = 0;
            }
        }
    }
}
