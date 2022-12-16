package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.move.EventBlockPushEntity;
import wtf.nebula.client.impl.event.impl.move.EventEntityCollision;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Velocity extends ToggleableModule {
    private final Property<Boolean> knockback = new Property<>(true, "Knockback", "antiknockback", "antikb");
    private final Property<Boolean> explosions = new Property<>(true, "Explosions", "expl");
    private final Property<Boolean> blocks = new Property<>(true, "Blocks", "noblockpush");
    private final Property<Boolean> collision = new Property<>(true, "Collision", "entitycollision");

    public Velocity() {
        super("Velocity", new String[]{"velocity", "antiknockback"}, ModuleCategory.MOVEMENT);
        offerProperties(knockback, explosions, blocks, collision);
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (!isNull()) {
            if (event.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = event.getPacket();
                if (packet.entityId == mc.thePlayer.getEntityId()) {
                    event.setCancelled(knockback.getValue());
                }
            } else if (event.getPacket() instanceof S27PacketExplosion) {
                event.setCancelled(explosions.getValue());
            }
        }
    }

    @EventListener
    public void onEntityCollision(EventEntityCollision event) {
        if (event.getTarget().equals(mc.thePlayer) && collision.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onBlockPush(EventBlockPushEntity event) {
        if (event.getEntity().equals(mc.thePlayer) && blocks.getValue()) {
            event.setCancelled(true);
        }
    }
}
