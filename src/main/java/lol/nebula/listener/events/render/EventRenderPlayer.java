package lol.nebula.listener.events.render;

import lol.nebula.listener.bus.CancelableEvent;
import lol.nebula.listener.events.EventStage;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author aesthetical
 * @since 05/04/23
 */
public class EventRenderPlayer extends CancelableEvent {
    private final EventStage stage;
    private final EntityPlayer player;

    public EventRenderPlayer(EventStage stage, EntityPlayer player) {
        this.stage = stage;
        this.player = player;
    }

    public EventStage getStage() {
        return stage;
    }

    public EntityPlayer getPlayer() {
        return player;
    }
}
