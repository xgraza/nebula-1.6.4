package wtf.nebula.client.impl.event.impl.player;

import me.bush.eventbus.event.Event;
import net.minecraft.potion.PotionEffect;

public class EventDeincrementPotionDuration extends Event {
    private final PotionEffect potionEffect;
    private final int duration;

    public EventDeincrementPotionDuration(PotionEffect potionEffect, int duration) {
        this.potionEffect = potionEffect;
        this.duration = duration;
    }

    public PotionEffect getPotionEffect() {
        return potionEffect;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
