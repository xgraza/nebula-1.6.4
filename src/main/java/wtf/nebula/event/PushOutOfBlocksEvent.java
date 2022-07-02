package wtf.nebula.event;

import me.bush.eventbus.event.Event;

public class PushOutOfBlocksEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }
}
