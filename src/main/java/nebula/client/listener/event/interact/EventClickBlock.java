package nebula.client.listener.event.interact;

import nebula.client.listener.bus.Cancelable;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class EventClickBlock extends Cancelable {
  private final int x, y, z, facing;

  public EventClickBlock(int x, int y, int z, int facing) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.facing = facing;
  }

  public int x() {
    return x;
  }

  public int y() {
    return y;
  }

  public int z() {
    return z;
  }

  public int face() {
    return facing;
  }
}
