package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class AntiHunger extends Module {
    public AntiHunger() {
        super("AntiHunger", ModuleCategory.WORLD);
    }

    public final Value<Boolean> sprint = new Value<>("Sprint", true);
    public final Value<Boolean> ground = new Value<>("Ground", true);

    @Override
    protected void onActivated() {
        super.onActivated();
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof C03PacketPlayer) {

            if (ground.getValue()) {
                ((C03PacketPlayer) event.getPacket()).onGround = true;
            }
        }

        else if (event.getPacket() instanceof C0BPacketEntityAction) {

            // 4 = START_SPRINT
            if (((C0BPacketEntityAction) event.getPacket()).action == 4) {
                event.setCancelled(true);
            }
        }
    }
}
