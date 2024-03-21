package nebula.client.gui.drawables;

/**
 * @author Gavin
 * @since 08/17/23
 */
public interface DrawableListener {

  void render(int mouseX, int mouseY, float partialTicks);
  void mouseClicked(int mouseX, int mouseY, int mouseButton);
  void keyTyped(char typedChar, int keyCode);

}
