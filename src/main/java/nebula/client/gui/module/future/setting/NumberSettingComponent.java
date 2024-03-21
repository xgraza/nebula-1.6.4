package nebula.client.gui.module.future.setting;

import nebula.client.gui.drawables.Component;
import nebula.client.module.impl.render.hud.HUDModule;
import nebula.client.util.math.MathUtils;
import nebula.client.util.render.ColorUtils;
import nebula.client.util.render.RenderUtils;
import nebula.client.util.value.Setting;
import org.lwjgl.input.Mouse;

import java.awt.*;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class NumberSettingComponent extends Component {
  private final Setting<Number> setting;

  private final double difference;
  private boolean dragging;

  public NumberSettingComponent(Setting<Number> setting) {
    this.setting = setting;

    this.difference = setting.max().doubleValue()
      - setting.min().doubleValue();
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    if (dragging && !Mouse.isButtonDown(0)) {
      dragging = false;
    }

    if (dragging) {
      setValue(mouseX);
    }

    Color clientColor = HUDModule.primary.value();

    double percent = (setting.value().doubleValue() - setting.min().doubleValue()) / difference;
    double barWidth = setting.value().doubleValue() < setting.min().doubleValue() ? 0.0 : width * percent;

    RenderUtils.gradientRect(x, y, barWidth, height,
      ColorUtils.alpha(clientColor.getRGB(), mouseOver(mouseX, mouseY) ? 55 : 77),
      ColorUtils.alpha(clientColor.getRGB(), mouseOver(mouseX, mouseY) ? 55 : 77));

    mc.fontRenderer.drawStringWithShadow(setting.meta().value(), (int) (x + 3), (int) (y + 4), -1);

    String formatted = String.format("%.1f", setting.value().doubleValue());
    mc.fontRenderer.drawStringWithShadow(formatted,
      (int) ((x + width) - (mc.fontRenderer.getStringWidth(formatted) + 2)),
      (int) (y + 4),
      0xBBBBBB);
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseOver(mouseX, mouseY) && mouseButton == 0) {
      dragging = true;
    }
  }

  @Override
  public void keyTyped(char typedChar, int keyCode) {

  }

  @Override
  public boolean visible() {
    return setting.visible();
  }

  private void setValue(double mouseX) {
    double value = setting.min().doubleValue() + difference * (mouseX - x) / width;
    double percision = 1.0 / setting.scale().doubleValue();
    value = Math.round(value * percision) / percision;
    value = MathUtils.round(value, 2);

    if (value > setting.max().doubleValue()) {
      value = setting.max().doubleValue();
    }

    if (value < setting.min().doubleValue()) {
      value = setting.min().doubleValue();
    }

    if (setting.value() instanceof Integer) {
      setting.setValue((int) value);
    } else if (setting.value() instanceof Double) {
      setting.setValue(value);
    } else if (setting.value() instanceof Float) {
      setting.setValue((float) value);
    }
  }
}
