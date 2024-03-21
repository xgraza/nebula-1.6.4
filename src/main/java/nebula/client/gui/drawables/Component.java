package nebula.client.gui.drawables;

import net.minecraft.client.Minecraft;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Gavin
 * @since 08/17/23
 */
public abstract class Component implements DrawableListener {

  private final List<Component> children = new LinkedList<>();
  protected final Minecraft mc = Minecraft.getMinecraft();
  protected double x, y, width, height;

  public List<Component> children() {
    return children;
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

  public double width() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double height() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  public boolean visible() {
    return true;
  }

  protected boolean mouseOver(int mouseX, int mouseY) {
    return mouseX >= x
      && mouseX <= x + width
      && mouseY >= y
      && mouseY <= y + height;
  }
}
