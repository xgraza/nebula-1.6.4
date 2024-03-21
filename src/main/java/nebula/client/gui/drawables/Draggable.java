package nebula.client.gui.drawables;

import org.lwjgl.input.Mouse;

/**
 * @author Gavin
 * @since 08/17/23
 */
public abstract class Draggable extends Component {

  private double dragX, dragY;
  private boolean dragging;

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    if (dragging) {
      setX(mouseX + dragX);
      setY(mouseY + dragY);

      if (!Mouse.isButtonDown(0)) dragging = false;
    }
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0 && mouseOver(mouseX, mouseY)) {
      dragging = true;
      dragX = x() - mouseX;
      dragY = y() - mouseY;
    }
  }
}
