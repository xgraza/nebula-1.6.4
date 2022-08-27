package wtf.nebula.client.impl.event.base;

import me.bush.eventbus.event.Event;

public abstract class EraBasedEvent extends Event {
    private final Era era;

    public EraBasedEvent(Era era) {
        this.era = era;
    }

    public Era getEra() {
        return era;
    }
}
