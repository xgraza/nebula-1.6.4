package nebula.client.gui.module.future;

import nebula.client.gui.drawables.Component;
import nebula.client.gui.drawables.Draggable;
import nebula.client.module.Module;
import nebula.client.module.ModuleCategory;
import nebula.client.module.impl.render.hud.HUDModule;
import nebula.client.util.render.ColorUtils;
import nebula.client.util.render.RenderUtils;
import nebula.client.util.system.AudioUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Gavin
 * @since 08/17/23
 */
public class CategoryPanel extends Draggable {

  private static final double SPACING = 1.5;
  private static final double PADDING = 2.0;

  /**
   * The location of the arrow
   */
  private static final ResourceLocation ARROW_LOCATION = new ResourceLocation(
    "nebula/textures/future/arrow.png");

  private final ModuleCategory moduleCategory;

  private boolean expanded;
  private float arrowAngle;

  public CategoryPanel(ModuleCategory moduleCategory,
                       List<Module> categorized) {

    this.moduleCategory = moduleCategory;

    expanded = true;
    arrowAngle = 180.0f;

    for (Module module : categorized) {
      children().add(new ModuleButton(module));
    }
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    super.render(mouseX, mouseY, partialTicks);

    RenderUtils.rect(x, y, width, height(), 0x77000000);

    Color clientColor = HUDModule.primary.value();

    RenderUtils.rect(x, y, width, height,
      ColorUtils.alpha(clientColor.getRGB(), 77));
    mc.fontRenderer.drawStringWithShadow(moduleCategory.display(),
      (int) (x + 3), (int) (y + 3), -1);

    {
      glPushMatrix();
      glEnable(GL_TEXTURE_2D);
      glEnable(GL_BLEND);
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);

      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

      mc.getTextureManager().bindTexture(ARROW_LOCATION);

      glTranslated((x + width) - 7, y + 7.5, 0.0);
      glRotatef(arrowAngle, 0, 0, 1);

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

    if (expanded) {
      if (arrowAngle > 0) arrowAngle -= 3 * partialTicks;

      double componentY = y + height + 1.0;
      for (Component component : children()) {
        component.setX(x + PADDING);
        component.setY(componentY);
        component.setWidth(width - (PADDING * 2));
        component.setHeight(height);

        component.render(mouseX, mouseY, partialTicks);
        componentY += component.height() + SPACING;
      }

    } else {
      if (arrowAngle < 180.0f) arrowAngle += 3 * partialTicks;
    }
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);

    if (mouseOver(mouseX, mouseY) && mouseButton == 1) {
      expanded = !expanded;
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
        originalHeight += component.height() + SPACING;
      }
    }

    return originalHeight + SPACING;
  }
}
