package us.nebula.impl.event.player;

import net.minecraft.entity.player.EntityPlayer;
import us.nebula.api.listener.Event;

/**
 * @author xgraza
 * @since 02/16/25
 */
public final class EventPlayerDeath extends Event
{
    private final EntityPlayer player;

    public EventPlayerDeath(final EntityPlayer player)
    {
        this.player = player;
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }
}
