package nebula.client.listener.event.render;

import nebula.client.listener.bus.Cancelable;
import net.minecraft.item.ItemStack;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class EventRenderStackCount extends Cancelable {
  private final ItemStack itemStack;
  private String text;
  private int color;

  public EventRenderStackCount(ItemStack itemStack, String text, int color) {
    this.itemStack = itemStack;
    this.text = text;
    this.color = color;
  }

  public ItemStack itemStack() {
    return itemStack;
  }

  public String text() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int color() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }
}
