package nebula.client.gui.module.future.setting;

import nebula.client.gui.drawables.Component;
import nebula.client.util.render.RenderUtils;
import nebula.client.util.system.AudioUtils;
import nebula.client.util.value.Setting;

import java.util.StringJoiner;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class EnumSettingComponent extends Component {
  private final Setting<Enum<?>> setting;

  public EnumSettingComponent(Setting<Enum<?>> setting) {
    this.setting = setting;
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    RenderUtils.gradientRect(x, y, width, height,
      mouseOver(mouseX, mouseY) ? 0x77AAAAAB : 0x33555555,
      mouseOver(mouseX, mouseY) ? 0x66AAAAAB : 0x55555555);

    mc.fontRenderer.drawStringWithShadow(setting.meta().value(), (int) (x + 3), (int) (y + 4), -1);

    String formatted = format(setting.value());
    mc.fontRenderer.drawStringWithShadow(formatted,
      (int) ((x + width) - (mc.fontRenderer.getStringWidth(formatted) + 2)),
      (int) (y + 4),
      0xBBBBBB);
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseOver(mouseX, mouseY)) {
      if (mouseButton == 0) {
        setting.nextEnum();
      } else if (mouseButton == 1) {
        setting.previousEnum();
      }
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

  public static String format(Enum<?> e) {
    if (!e.name().equals(e.toString())) return e.toString();

    StringJoiner builder = new StringJoiner(" ");
    for (String word : e.name().split("_")) {
      builder.add(Character.toUpperCase(word.charAt(0))
        + word.substring(1).toLowerCase());
    }

    return builder.toString();
  }
}
