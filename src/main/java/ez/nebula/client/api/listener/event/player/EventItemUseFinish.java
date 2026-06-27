package ez.nebula.client.api.listener.event.player;

import ez.nebula.client.api.listener.Event;
import net.minecraft.item.ItemStack;

public final class EventItemUseFinish extends Event
{
    private final ItemStack itemStack;

    public EventItemUseFinish(ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack()
    {
        return itemStack;
    }
}
