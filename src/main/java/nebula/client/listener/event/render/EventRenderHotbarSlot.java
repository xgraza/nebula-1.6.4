package nebula.client.listener.event.render;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class EventRenderHotbarSlot {

  private int slot;

  public EventRenderHotbarSlot(int slot) {
    this.slot = slot;
  }

  public int slot() {
    return slot;
  }

  public void setSlot(int slot) {
    this.slot = slot;
  }
}
