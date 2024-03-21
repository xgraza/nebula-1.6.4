package nebula.client.module.impl.render.esp;

import nebula.client.Nebula;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.render.EventRender2D;
import nebula.client.listener.event.render.EventRender3D;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.player.FakePlayerUtils;
import nebula.client.util.render.ProjectionUtils;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Gavin
 * @since 08/23/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "ESP", description = "yes")
public class ESPModule extends Module {

  @SettingMeta("Mode")
  private final Setting<ESPMode> mode = new Setting<>(
    ESPMode.CS_GO);
  @SettingMeta("Labels")
  private final Setting<Boolean> labels = new Setting<>(
    () -> mode.value() == ESPMode.CS_GO,
    false);

  private final Map<Integer, float[][]> projected = new ConcurrentHashMap<>();

  @Override
  public void disable() {
    super.disable();
    projected.clear();
  }

  @Subscribe
  private final Listener<EventRender2D> render2D = event -> {

    if (mode.value() == ESPMode.CS_GO) {

      if (mc.thePlayer.ticksExisted < 5) {
        projected.clear();
        return;
      }

      for (int id : projected.keySet()) {
        float[][] projection = projected.get(id);
        if (projection == null) continue;

        Entity entity = mc.theWorld.getEntityByID(id);
        if (!(entity instanceof EntityLivingBase e) || entity.isDead) {
          projected.remove(id);
          continue;
        }

        if (id == mc.thePlayer.getEntityId()
          && mc.gameSettings.thirdPersonView == 0) continue;

        float[] top = projection[0];
        float[] bottom = projection[1];

        if (top[2] > 1 || bottom[2] > 1) {
          projected.remove(id);
          continue;
        }

        double height = top[1] - bottom[1];
        double width = height * 0.3;

        glPushMatrix();

        glLineWidth(2.5f);

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_TEXTURE_2D);

        if (e instanceof EntityPlayer player
          && (Nebula.INSTANCE.friend.isFriend(player)
          || player.equals(mc.thePlayer))) {
          glColor4f(0, 0.4f, 0.6f, 1);
        } else {

          if (FakePlayerUtils.isFake(e.getEntityId())) {
            glColor4f(0.3f, 0.3f, 0.3f, 1);
          } else {
            glColor4f(1, 1, 1, 1);
          }
        }

        glScaled(0.5, 0.5, 0.5);

        glBegin(GL_LINES);
        {
          glVertex2d(top[0] - width, top[1]);
          glVertex2d(top[0] + width, top[1]);

          glVertex2d(top[0] - width, top[1]);
          glVertex2d(top[0] - width, bottom[1]);

          glVertex2d(top[0] + width, top[1]);
          glVertex2d(top[0] + width, bottom[1]);

          glVertex2d(top[0] - width, bottom[1]);
          glVertex2d(top[0] + width, bottom[1]);
        }
        glEnd();

        // heath bar

        float healthPercent = e.getHealth() / 24.0f;

        glColor4f(
          1.0f - healthPercent,
          healthPercent,
          0.0f,
          1.0f
        );

        glBegin(GL_LINES);
        {
          glVertex2d(top[0] + (height * 0.35), top[1]);
          glVertex2d(top[0] + (height * 0.35), bottom[1]);
        }
        glEnd();

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);

        if (labels.value()) {
          String name = e.getCommandSenderName() + EnumChatFormatting.RED + " " + e.getHealth() + "\u2764";
          int textWidth = mc.fontRenderer.getStringWidth(name);
          mc.fontRenderer.drawStringWithShadow(name,
            (int) (top[0] - (textWidth / 2)), (int) top[1] - 10, -1);
        }

        glPopMatrix();
      }
    }
  };

  @Subscribe
  private final Listener<EventRender3D> render3D = event -> {

    if (mode.value() == ESPMode.CS_GO) {
      for (Entity e : (List<EntityPlayer>) mc.theWorld.playerEntities) {
        if (e == null || e.isDead) continue;

        if (e.equals(mc.thePlayer) && mc.gameSettings.thirdPersonView == 0) continue;

        double x = (e.lastTickPosX + (e.posX - e.lastTickPosX) * event.tickDelta()) - RenderManager.renderPosX;
        double y = (e.lastTickPosY + (e.posY - e.lastTickPosY) * event.tickDelta()) - RenderManager.renderPosY;
        double z = (e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * event.tickDelta()) - RenderManager.renderPosZ;

        if (e.equals(mc.thePlayer)) {
          y -= mc.thePlayer.yOffset;
        }

        float[] top = ProjectionUtils.project(x, y + e.height + 0.2, z);
        float[] bottom = ProjectionUtils.project(x, y - 0.2, z);

        projected.put(e.getEntityId(), new float[][]{top, bottom});
      }
    }
  };
}
