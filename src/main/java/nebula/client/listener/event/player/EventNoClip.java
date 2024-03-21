package nebula.client.listener.event.player;

import nebula.client.listener.bus.Cancelable;
import net.minecraft.entity.Entity;

/**
 * @author Gavin
 * @since 08/26/23
 */
public class EventNoClip extends Cancelable {
  private final Entity entity;

  public EventNoClip(Entity entity) {
    this.entity = entity;
  }

  public Entity entity() {
    return entity;
  }
}
