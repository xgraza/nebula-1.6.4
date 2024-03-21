package nebula.client.listener.event.player.rotate;

import nebula.client.listener.bus.Cancelable;
import net.minecraft.entity.EntityLivingBase;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class EventHeadRotations extends Cancelable {
  private final EntityLivingBase entity;

  public EventHeadRotations(EntityLivingBase entity) {
    this.entity = entity;
  }

  public EntityLivingBase entity() {
    return entity;
  }
}
