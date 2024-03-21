package nebula.client.gui.module.future;

import nebula.client.gui.drawables.Component;
import nebula.client.gui.module.future.setting.BooleanSettingComponent;
import nebula.client.gui.module.future.setting.EnumSettingComponent;
import nebula.client.gui.module.future.setting.MacroSettingComponent;
import nebula.client.gui.module.future.setting.NumberSettingComponent;
import nebula.client.macro.Macro;
import nebula.client.module.Module;
import nebula.client.module.impl.render.hud.HUDModule;
import nebula.client.util.render.ColorUtils;
import nebula.client.util.render.RenderUtils;
import nebula.client.util.system.AudioUtils;
import nebula.client.util.value.Setting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Gavin
 * @since 08/17/23
 */
@SuppressWarnings("unchecked")
public class ModuleButton extends Component {

  private static final double PADDING = 1.0;
  private static final double SPACING = 1.5;

  /**
   * The location of the gear
   */
  private static final ResourceLocation GEAR_LOCATION = new ResourceLocation(
    "nebula/textures/future/gear.png");

  private final Module module;

  private boolean expanded;
  private float gearAngle;

  public ModuleButton(Module module) {
    this.module = module;

    for (Setting<?> setting : module.settings()) {
      if (setting.value() instanceof Boolean) {
        children().add(new BooleanSettingComponent(
          (Setting<Boolean>) setting));
      } else if (setting.value() instanceof Enum<?>) {
        children().add(new EnumSettingComponent(
          (Setting<Enum<?>>) setting));
      } else if (setting.value() instanceof Number) {
        children().add(new NumberSettingComponent(
          (Setting<Number>) setting));
      } else if (setting.value() instanceof Macro macro) {
        children().add(new MacroSettingComponent(setting.meta().value(), macro));
      }
    }

    children().add(new MacroSettingComponent("Bind", module.macro()));
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    Color clientColor = HUDModule.primary.value();

    int topColor;
    if (module.macro().toggled()) {
      topColor = ColorUtils.alpha(clientColor.getRGB(), mouseOver(mouseX, mouseY) ? 55 : 77);
    } else {
      topColor = mouseOver(mouseX, mouseY) ? 0x77AAAAAB : 0x33555555;
    }

    int bottomColor;
    if (module.macro().toggled()) {
      bottomColor = ColorUtils.alpha(clientColor.getRGB(), mouseOver(mouseX, mouseY) ? 55 : 77);
    } else {
      bottomColor = mouseOver(mouseX, mouseY) ? 0x66AAAAAB : 0x55555555;
    }

    RenderUtils.gradientRect(x, y, width, height, topColor, bottomColor);

    if (expanded) {
      gearAngle += 1;

      double componentY = y + height + 1.0;
      for (Component component : children()) {
        if (!component.visible()) continue;

        component.setX(x + PADDING);
        component.setY(componentY);
        component.setWidth(width - (PADDING * 2));
        component.setHeight(height);

        component.render(mouseX, mouseY, partialTicks);
        componentY += component.height() + SPACING;
      }
    }

    {
      glPushMatrix();
      glEnable(GL_TEXTURE_2D);
      glEnable(GL_BLEND);
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);

      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

      mc.getTextureManager().bindTexture(GEAR_LOCATION);

      glTranslated((x + width) - 7, y + 7.5, 0.0);
      glRotatef(gearAngle, 0, 0, 1);

      // Minecraft#draw
      float f = 0.00390625f;
      glColor4f(255 * f,
        255 * f,
        255 * f,
        255 * f);
      Gui.func_146110_a(-5, -5,
        0, 0,
        10, 10,
        10, 10);

      glBindTexture(GL_TEXTURE_2D, 0);

      glDisable(GL_BLEND);
      glPopMatrix();
    }

    mc.fontRenderer.drawStringWithShadow(module.meta().name(), (int) (x + 3), (int) (y + 4), -1);
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseOver(mouseX, mouseY)) {
      if (mouseButton == 0) {
        module.macro().setEnabled(!module.macro().toggled());
      } else if (mouseButton == 1) {
        expanded = !expanded;
      }
      AudioUtils.click();
    }

    if (expanded) {
      for (Component component : children()) {
        component.mouseClicked(mouseX, mouseY, mouseButton);
      }
    }
  }

  @Override
  public void keyTyped(char typedChar, int keyCode) {
    if (expanded) {
      for (Component component : children()) {
        component.keyTyped(typedChar, keyCode);
      }
    }
  }

  @Override
  public double height() {
    double originalHeight = super.height();

    if (expanded) {
      for (Component component : children()) {
        if (!component.visible()) continue;
        originalHeight += component.height() + SPACING;
      }
    }

    return originalHeight;
  }
}
