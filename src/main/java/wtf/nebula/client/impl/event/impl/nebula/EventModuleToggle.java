package wtf.nebula.client.impl.event.impl.nebula;

import me.bush.eventbus.event.Event;
import wtf.nebula.client.impl.module.ToggleableModule;

public class EventModuleToggle extends Event {
    private final ToggleableModule module;

    public EventModuleToggle(ToggleableModule module) {
        this.module = module;
    }

    public ToggleableModule getModule() {
        return module;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
