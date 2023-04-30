package lol.nebula.listener.events.entity;

import lol.nebula.listener.bus.CancelableEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author aesthetical
 * @since 04/30/23
 */
public class EventAttack extends CancelableEvent {
    private final EntityPlayer attacker;
    private final Entity target;

    public EventAttack(EntityPlayer attacker, Entity target) {
        this.attacker = attacker;
        this.target = target;
    }

    public EntityPlayer getAttacker() {
        return attacker;
    }

    public Entity getTarget() {
        return target;
    }
}
