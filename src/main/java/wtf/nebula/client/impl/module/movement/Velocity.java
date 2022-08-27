package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import wtf.nebula.client.impl.event.impl.network.PacketEvent;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Velocity extends ToggleableModule {
    public Velocity() {
        super("Velocity", new String[]{"velocity", "antiknockback"}, ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            event.setCancelled(true);
        } else if (event.getPacket() instanceof S27PacketExplosion) {
            event.setCancelled(true);
        }
    }
}
