package nebula.client.gui.module.future.setting;

import nebula.client.gui.drawables.Component;
import nebula.client.module.impl.render.hud.HUDModule;
import nebula.client.util.render.ColorUtils;
import nebula.client.util.render.RenderUtils;
import nebula.client.util.system.AudioUtils;
import nebula.client.util.value.Setting;

import java.awt.*;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class BooleanSettingComponent extends Component {
  private final Setting<Boolean> setting;

  public BooleanSettingComponent(Setting<Boolean> setting) {
    this.setting = setting;
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    Color clientColor = HUDModule.primary.value();

    int topColor;
    if (setting.value()) {
      topColor = ColorUtils.alpha(clientColor.getRGB(), mouseOver(mouseX, mouseY) ? 55 : 77);
    } else {
      topColor = mouseOver(mouseX, mouseY) ? 0x77AAAAAB : 0x33555555;
    }

    int bottomColor;
    if (setting.value()) {
      bottomColor = ColorUtils.alpha(clientColor.getRGB(), mouseOver(mouseX, mouseY) ? 55 : 77);
    } else {
      bottomColor = mouseOver(mouseX, mouseY) ? 0x66AAAAAB : 0x55555555;
    }

    RenderUtils.gradientRect(x, y, width, height, topColor, bottomColor);

    mc.fontRenderer.drawStringWithShadow(setting.meta().value(), (int) (x + 3), (int) (y + 4), -1);
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseOver(mouseX, mouseY) && mouseButton == 0) {
      setting.setValue(!setting.value());
      AudioUtils.click();
    }
  }

  @Override
  public void keyTyped(char typedChar, int keyCode) {

  }

  @Override
  public boolean visible() {
    return setting.visible();
  }
}
