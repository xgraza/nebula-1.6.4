package wtf.nebula.event;

import me.bush.eventbus.event.Event;
import net.minecraft.src.EntityPlayer;

public class DeathEvent extends Event {
    private final EntityPlayer player;

    public DeathEvent(EntityPlayer player) {
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
