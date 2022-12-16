package wtf.nebula.client.impl.event.base;

import me.bush.eventbus.event.Event;

public abstract class EventEraed extends Event {
    private final Era era;

    public EventEraed(Era era) {
        this.era = era;
    }

    public Era getEra() {
        return era;
    }
}
