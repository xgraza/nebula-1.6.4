package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet10Flying;
import net.minecraft.src.Packet19EntityAction;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class AntiHunger extends Module {
    public AntiHunger() {
        super("AntiHunger", ModuleCategory.MOVEMENT);
    }

    public final Value<Boolean> sprint = new Value<>("Sprint", true);
    public final Value<Boolean> ground = new Value<>("Ground", true);

    @Override
    protected void onActivated() {
        super.onActivated();
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet10Flying) {

            if (ground.getValue()) {
                ((Packet10Flying) event.getPacket()).onGround = true;
            }
        }

        else if (event.getPacket() instanceof Packet19EntityAction) {

            // 4 = START_SPRINT
            if (((Packet19EntityAction) event.getPacket()).action == 4) {
                event.setCancelled(true);
            }
        }
    }
}
