package nebula.client.gui.module.future;

import io.sentry.Sentry;
import nebula.client.Nebula;
import nebula.client.module.Module;
import nebula.client.module.ModuleCategory;
import nebula.client.module.impl.render.clickgui.ClickGUIModule;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Gavin
 * @since 08/17/23
 */
public class FutureModuleConfigScreen extends GuiScreen {
  private final List<CategoryPanel> panelList = new LinkedList<>();
  private final ClickGUIModule module;

  public FutureModuleConfigScreen(ClickGUIModule module) {
    this.module = module;

    double x = 4.0;
    double y = 4.0;

    for (ModuleCategory moduleCategory : ModuleCategory.values()) {
      List<Module> categorized = Nebula.INSTANCE.module.values()
        .stream()
        .filter((mod) -> mod.category() == moduleCategory)
        .toList();
      if (categorized.isEmpty()) continue;

      CategoryPanel categoryPanel = new CategoryPanel(
        moduleCategory, categorized);

      categoryPanel.setX(x);
      categoryPanel.setY(y);
      categoryPanel.setWidth(88.0);
      categoryPanel.setHeight(15.0);

      panelList.add(categoryPanel);

      x += categoryPanel.width() + 4.0;
    }
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float tickDelta) {
    int scroll = Mouse.getDWheel();
    if (scroll > 0) {
      for (CategoryPanel categoryPanel : panelList) {
        double y = categoryPanel.y() + 10.0;
        if (y + 20 < height) categoryPanel.setY(y);
      }
    } else if (scroll < 0) {
      for (CategoryPanel categoryPanel : panelList) {
        double y = categoryPanel.y() - 10.0;
        if (y + 20 > 0.0) categoryPanel.setY(y);
      }
    }

    for (CategoryPanel categoryPanel : panelList) {
      categoryPanel.render(mouseX, mouseY, tickDelta);
    }
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);

    for (CategoryPanel categoryPanel : panelList) {
      categoryPanel.mouseClicked(mouseX, mouseY, mouseButton);
    }
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) {
    super.keyTyped(typedChar, keyCode);

    for (CategoryPanel categoryPanel : panelList) {
      categoryPanel.keyTyped(typedChar, keyCode);
    }
  }

  @Override
  public void onGuiClosed() {
    super.onGuiClosed();
    module.macro().setEnabled(false);

    try {
      Nebula.LOGGER.info(Nebula.INSTANCE.module.save(
        "default"));
    } catch (IOException e) {
      e.printStackTrace();
      Sentry.captureException(e);
    }
  }

  @Override
  public boolean doesGuiPauseGame() {
    return module.pause.value();
  }
}
