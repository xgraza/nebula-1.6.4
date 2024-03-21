package nebula.client.listener.event.player;

import nebula.client.listener.bus.Cancelable;
import nebula.client.listener.event.EventStage;

/**
 * @author Gavin
 * @since 08/10/23
 */
public class EventMoveUpdate extends Cancelable {
  private final EventStage stage;
  private double x, y, stance, z;
  private float yaw, pitch;
  private boolean ground;

  public EventMoveUpdate(EventStage stage, double x, double y, double stance, double z, float yaw, float pitch, boolean ground) {
    this.stage = stage;
    this.x = x;
    this.y = y;
    this.stance = stance;
    this.z = z;
    this.yaw = yaw;
    this.pitch = pitch;
    this.ground = ground;
  }

  public EventStage stage() {
    return stage;
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

  public double stance() {
    return stance;
  }

  public void setStance(double stance) {
    this.stance = stance;
  }

  public double z() {
    return z;
  }

  public void setZ(double z) {
    this.z = z;
  }

  public float yaw() {
    return yaw;
  }

  public void setYaw(float yaw) {
    this.yaw = yaw;
  }

  public float pitch() {
    return pitch;
  }

  public void setPitch(float pitch) {
    this.pitch = pitch;
  }

  public boolean ground() {
    return ground;
  }

  public void setGround(boolean ground) {
    this.ground = ground;
  }
}
