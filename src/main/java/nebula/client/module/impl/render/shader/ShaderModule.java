package nebula.client.module.impl.render.shader;

import io.sentry.Sentry;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.render.EventRender3D;
import nebula.client.listener.event.render.EventRenderEntity;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.module.impl.render.hud.HUDModule;
import nebula.client.util.chat.Printer;
import nebula.client.util.render.ColorUtils;
import nebula.client.util.render.RenderUtils;
import nebula.client.util.render.shader.Shader;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Gavin
 * @since 08/14/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "Shader",
  description = "Renders a shader overlay",
  defaultState = true)
public class ShaderModule extends Module {

  @SettingMeta("Distance")
  private final Setting<Double> distance = new Setting<>(
    50.0, 6.0, 200.0, 0.1);

  private final Shader shader = new Shader(
    "/assets/minecraft/nebula/shader/vertex.vsh",
    "/assets/minecraft/nebula/shader/esp.fsh",
    (s) -> {
      s.createUniform("texture");
      s.createUniform("texelSize");
      s.createUniform("color");
      s.createUniform("radius");
      s.createUniform("opacity");
    });

  private Framebuffer fb;
  private int width, height, scale;

  @Subscribe
  private final Listener<EventRender3D> render3D = event -> {
    // shaders do not work with optifine fast render
    if (mc.gameSettings.ofFastRender) {
      macro().setEnabled(false);
      return;
    }

    ScaledResolution r = RenderUtils.resolution;
    if (r == null) return;

    glPushMatrix();
    glPushAttrib(GL_ALPHA_BITS);

    glEnable(GL_ALPHA_TEST);

    if (fb != null) {
      fb.framebufferClear();

      if (r.getScaledWidth() != width
        || r.getScaledHeight() != height
        || r.getScaleFactor() != scale) {

        width = r.getScaledWidth();
        height = r.getScaledHeight();
        scale = r.getScaleFactor();

        fb.deleteFramebuffer();
        fb = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
      }
    } else {
      fb = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    }

    fb.bindFramebuffer(false);

    boolean s = Render.renderShadow;
    Render.renderShadow = false;

    List<Entity> entities = new ArrayList<>(mc.theWorld.loadedEntityList);
    for (Entity entity : entities) {
      if (canRenderEntity(entity)) {
        RenderManager.instance.renderEntityStatic(entity, event.tickDelta(), true);
        //renderEntity(entity, event.tickDelta());
      }
    }

    fb.unbindFramebuffer();
    mc.getFramebuffer().bindFramebuffer(true);

    shader.use();

    shader.set("texture", 0);
    shader.set("texelSize", 1.0f / r.getScaledWidth(), 1.0f / r.getScaledHeight());

    Color c = ColorUtils.pulse(HUDModule.primary.value(), HUDModule.secondary.value(), 10, 1);
    shader.set("color", c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f);
    shader.set("radius", 1.0f);
    shader.set("opacity", 0.0f);

    mc.entityRenderer.disableLightmap(0.0);
    RenderHelper.disableStandardItemLighting();

    mc.entityRenderer.setupOverlayRendering();
    glEnable(GL_TEXTURE_2D);

    glBindTexture(GL_TEXTURE_2D, fb.framebufferTexture);
    glBegin(GL_QUADS);
    {
      glTexCoord2d(0, 1);
      glVertex2d(0, 0);
      glTexCoord2d(0, 0);
      glVertex2d(0, r.getScaledHeight());
      glTexCoord2d(1, 0);
      glVertex2d(r.getScaledWidth(), r.getScaledHeight());
      glTexCoord2d(1, 1);
      glVertex2d(r.getScaledWidth(), 0);
    }
    glEnd();

    shader.stop();

    Render.renderShadow = s;

    glPopAttrib();
    glPopMatrix();
  };

  @Subscribe
  private final Listener<EventRenderEntity> renderEntity = event -> {
    if (!canRenderEntity(event.entity())) {
      event.setCanceled(true);
    }
  };

  // TODO: this doesnt render the entity lol...
  private void renderEntity(Entity e, float tickDelta) {
    Render render = RenderManager.instance.getEntityRenderObject(e);
    if (render != null && RenderManager.instance.renderEngine != null) {
      if (e.ticksExisted == 0) {
        e.lastTickPosX = e.posX;
        e.lastTickPosY = e.posY;
        e.lastTickPosZ = e.posZ;
      }

      double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) tickDelta;
      double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) tickDelta;
      double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) tickDelta;
      float yaw = e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * tickDelta;

      int brightness = e.getBrightnessForRender(tickDelta);
      if (e.isBurning()) brightness = 15728880;

      int var12 = brightness % 65536;
      int var13 = brightness / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) var12, (float) var13);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

      try {
        render.doRender(e,
          x - RenderManager.renderPosX,
          y - RenderManager.renderPosY,
          z - RenderManager.renderPosZ,
          yaw, tickDelta);
      } catch (Exception ex) {
        ex.printStackTrace();
        Sentry.captureException(ex);
      }
    }
  }

  private boolean canRenderEntity(Entity entity) {
    if (!(entity instanceof EntityLivingBase)) return false;
    return !entity.equals(mc.thePlayer) || mc.gameSettings.thirdPersonView != 0;
  }
}
