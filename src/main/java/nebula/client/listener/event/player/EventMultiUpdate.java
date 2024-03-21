package nebula.client.listener.event.player;

/**
 * @author Gavin
 * @since 08/25/23
 */
public class EventMultiUpdate {
  private int updates = 1;

  public int updates() {
    return updates;
  }

  public void setUpdates(int updates) {
    this.updates = updates;
  }
}
