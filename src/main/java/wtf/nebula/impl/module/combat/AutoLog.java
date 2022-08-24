package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.ChatComponentText;
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
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Double.NaN, Double.NaN, Double.NaN, Double.NaN, false));
            }

            mc.thePlayer.sendQueue.handleDisconnect(new S40PacketDisconnect(new ChatComponentText("fuck yo bitch ass")));

            if (autoDisable.getValue()) {
                setState(false);
            }
        }
    }
}
