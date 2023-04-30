package lol.nebula.listener.events.entity;

import lol.nebula.listener.bus.CancelableEvent;
import net.minecraft.potion.PotionEffect;

/**
 * @author aesthetical
 * @since 04/30/23
 */
public class EventDecrementPotion extends CancelableEvent {
    private final PotionEffect effect;

    public EventDecrementPotion(PotionEffect effect) {
        this.effect = effect;
    }

    public PotionEffect getEffect() {
        return effect;
    }
}
