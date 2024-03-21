package nebula.client.listener.event.player;

import nebula.client.listener.bus.Cancelable;

/**
 * @author Gavin
 * @since 08/23/23
 */
public class EventMove extends Cancelable {
  private double x, y, z;

  public EventMove(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public double x() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double y() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double z() {
    return z;
  }

  public void setZ(double z) {
    this.z = z;
  }
}
