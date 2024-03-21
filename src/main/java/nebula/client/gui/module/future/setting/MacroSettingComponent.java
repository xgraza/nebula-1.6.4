package nebula.client.gui.module.future.setting;

import nebula.client.gui.drawables.Component;
import nebula.client.macro.Macro;
import nebula.client.macro.MacroType;
import nebula.client.module.impl.render.hud.HUDModule;
import nebula.client.util.render.ColorUtils;
import nebula.client.util.render.RenderUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * @author Gavin
 * @since 08/24/23
 */
public class MacroSettingComponent extends Component {

  private final String name;
  private final Macro macro;
  private boolean listening;

  public MacroSettingComponent(String name, Macro macro) {
    this.name = name;
    this.macro = macro;
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    Color clientColor = HUDModule.primary.value();

    int topColor;
    if (listening) {
      topColor = ColorUtils.alpha(clientColor.getRGB(), mouseOver(mouseX, mouseY) ? 55 : 77);
    } else {
      topColor = mouseOver(mouseX, mouseY) ? 0x77AAAAAB : 0x33555555;
    }

    int bottomColor;
    if (listening) {
      bottomColor = ColorUtils.alpha(clientColor.getRGB(), mouseOver(mouseX, mouseY) ? 55 : 77);
    } else {
      bottomColor = mouseOver(mouseX, mouseY) ? 0x66AAAAAB : 0x55555555;
    }

    RenderUtils.gradientRect(x, y, width, height, topColor, bottomColor);

    mc.fontRenderer.drawStringWithShadow(listening ? "Listening..." : name, (int) (x + 3), (int) (y + 4), -1);

    if (!listening) {
      String formatted = switch (macro.type()) {
        case KEYBOARD -> {
          if (macro.key() == -1) yield "NONE";
          yield Keyboard.getKeyName(macro.key());
        }
        case MOUSE -> {
          if (macro.key() == -1) yield "NONE";
          yield "MB " + (macro.key() + 1);
        }
      };
      mc.fontRenderer.drawStringWithShadow(formatted,
        (int) ((x + width) - (mc.fontRenderer.getStringWidth(formatted) + 2)),
        (int) (y + 4),
        0xBBBBBB);
    }
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (listening) {
      listening = false;

      macro.setKey(mouseButton);
      macro.setType(MacroType.MOUSE);
    }

    if (mouseOver(mouseX, mouseY)) {

      if (mouseButton == 0) {
        listening = !listening;
      } else if (mouseButton == 1) {
        listening = false;
        macro.setKey(-1);
      }
    }
  }

  @Override
  public void keyTyped(char typedChar, int keyCode) {
    if (listening) {
      listening = false;
      macro.setKey(keyCode);
      macro.setType(MacroType.KEYBOARD);
    }
  }
}
