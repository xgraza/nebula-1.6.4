package nebula.client.module.impl.render.chams;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.render.EventRenderLiving;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Gavin
 * @since 08/18/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "Chams",
  description = "Show visible entities")
public class ChamsModule extends Module {

  // colored
  @SettingMeta("Colored")
  private final Setting<Boolean> colored = new Setting<>(
    false);
  @SettingMeta("Mode")
  private final Setting<ChamsMode> mode = new Setting<>(
    colored::value, ChamsMode.FILL);
  @SettingMeta("Visible Color")
  private final Setting<Color> visibleColor = new Setting<>(
    colored::value, new Color(0, 255, 0));
  @SettingMeta("Hidden Color")
  private final Setting<Color> hiddenColor = new Setting<>(
    colored::value, new Color(255, 0, 0));

  // render settings
  @SettingMeta("Textured")
  private final Setting<Boolean> textured = new Setting<>(
    true);
  @SettingMeta("Lighting")
  private final Setting<Boolean> lighting = new Setting<>(
    false);

  // types/entities
  @SettingMeta("Players")
  private final Setting<Boolean> players = new Setting<>(
    true);
  @SettingMeta("Containers")
  private final Setting<Boolean> containers = new Setting<>(
    true);

  @Subscribe
  private final Listener<EventRenderLiving> renderLiving = event -> {
    if (!(event.entity() instanceof EntityPlayer) || !players.value()) return;

    event.setCanceled(true);

    glPushMatrix();
    glPushAttrib(GL_ALL_ATTRIB_BITS);

    if (!textured.value()) {
      glDisable(GL_TEXTURE_2D);
    }

    if (colored.value()) {

      if (mode.value() == ChamsMode.LINE && textured.value()) {
        event.renderModel();
      }

      if (!lighting.value()) {
        glDisable(GL_LIGHTING);
      }

      glDepthMask(false);
      glDisable(GL_DEPTH_TEST);

      glPolygonMode(GL_FRONT_AND_BACK, mode.value().cap());

      Color c = hiddenColor.value();
      glColor4f(c.getRed() / 255.0f,
        c.getGreen() / 255.0f,
        c.getBlue() / 255.0f,
        1);

      event.renderModel();

      glEnable(GL_DEPTH_TEST);
      glDepthMask(true);

      glPolygonMode(GL_FRONT_AND_BACK, mode.value().cap());
      c = visibleColor.value();
      glColor4f(c.getRed() / 255.0f,
        c.getGreen() / 255.0f,
        c.getBlue() / 255.0f,
        1);

      event.renderModel();

      if (!lighting.value()) {
        glEnable(GL_LIGHTING);
      }
    } else {
      glEnable(GL_POLYGON_OFFSET_FILL);
      glPolygonOffset(1.0f, -110000.0f);

      event.renderModel();

      glPolygonOffset(1.0f, 110000.0f);
      glDisable(GL_POLYGON_OFFSET_FILL);
    }

    if (!textured.value()) {
      glEnable(GL_TEXTURE_2D);
    }

    glPopAttrib();
    glPopMatrix();

  };

}
