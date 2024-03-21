package nebula.client.gui.module.nebula;

import nebula.client.module.impl.render.clickgui.ClickGUIModule;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author Gavin
 * @since 08/09/23
 */
public class ModuleConfigScreen extends GuiScreen {

  private final ClickGUIModule clickGui;

  public ModuleConfigScreen(ClickGUIModule clickGui) {
    this.clickGui = clickGui;
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float tickDelta) {
    super.drawScreen(mouseX, mouseY, tickDelta);

  }

  @Override
  public void onGuiClosed() {
    super.onGuiClosed();
    clickGui.macro().setEnabled(false);
  }
}
