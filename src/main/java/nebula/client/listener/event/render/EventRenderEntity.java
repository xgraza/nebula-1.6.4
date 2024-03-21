package nebula.client.listener.event.render;

import nebula.client.listener.bus.Cancelable;
import net.minecraft.entity.Entity;

/**
 * @author Gavin
 * @since 08/15/23
 */
public class EventRenderEntity extends Cancelable {
  private final Entity entity;

  public EventRenderEntity(Entity entity) {
    this.entity = entity;
  }

  public Entity entity() {
    return entity;
  }
}
