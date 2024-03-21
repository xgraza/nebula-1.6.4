package nebula.client.listener.event.render;

import nebula.client.listener.bus.Cancelable;
import net.minecraft.entity.item.EntityXPOrb;

/**
 * @author Gavin
 * @since 08/24/23
 */
public class EventRenderEXPOrb extends Cancelable {
  private final EntityXPOrb entity;

  public EventRenderEXPOrb(EntityXPOrb entity) {
    this.entity = entity;
  }

  public EntityXPOrb entity() {
    return entity;
  }
}
