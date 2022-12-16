package wtf.nebula.client.impl.event.impl.player;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.player.EntityPlayer;

public class EventSwingArm extends Event {
    private final EntityPlayer player;

    public EventSwingArm(EntityPlayer player) {
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
