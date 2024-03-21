package nebula.client.listener.event.render;

import nebula.client.listener.bus.Cancelable;
import net.minecraft.entity.item.EntityItem;

/**
 * @author Gavin
 * @since 08/24/23
 */
public class EventRender3DItem extends Cancelable {

  private final EntityItem entity;

  public EventRender3DItem(EntityItem entity) {
    this.entity = entity;
  }

  public EntityItem entity() {
    return entity;
  }
}
