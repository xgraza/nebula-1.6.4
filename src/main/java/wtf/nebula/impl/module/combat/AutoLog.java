package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.GuiDisconnected;
import net.minecraft.src.Packet11PlayerPosition;
import net.minecraft.src.Packet255KickDisconnect;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class AutoLog extends Module {
    public AutoLog() {
        super("AutoLog", ModuleCategory.COMBAT);
    }

    public final Value<Float> health = new Value<>("Health", 10.0f, 1.0f, 20.0f);
    public final Value<Boolean> invalidPacket = new Value<>("InvalidPacket", false);
    public final Value<Boolean> autoDisable = new Value<>("AutoDisable", true);

    @EventListener
    public void onTick(TickEvent event) {

        if (mc.thePlayer.getHealth() <= health.getValue()) {

            if (invalidPacket.getValue()) {
                mc.thePlayer.sendQueue.addToSendQueue(new Packet11PlayerPosition(Double.NaN, Double.NaN, Double.NaN, Double.NaN, false));
            }

            mc.thePlayer.sendQueue.quitWithPacket(new Packet255KickDisconnect("Quitting"));

            if (autoDisable.getValue()) {
                setState(false);
            }
        }
    }
}
