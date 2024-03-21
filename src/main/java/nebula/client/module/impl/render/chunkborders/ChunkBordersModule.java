package nebula.client.module.impl.render.chunkborders;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.render.EventRender3D;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.module.impl.render.hud.HUDModule;
import nebula.client.util.render.RenderUtils;
import net.minecraft.client.renderer.entity.RenderManager;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Gavin
 * @since 03/20/24
 */
@ModuleMeta(name = "ChunkBorders",
  description = "Shows the chunk boundaries")
public final class ChunkBordersModule extends Module {

  @Subscribe
  private final Listener<EventRender3D> render2DListener = event -> {

    final double renderX = RenderManager.renderPosX;
    final double renderY = RenderManager.renderPosY;
    final double renderZ = RenderManager.renderPosZ;

    final int minX = mc.thePlayer.chunkCoordX * 16;
    final int minZ = mc.thePlayer.chunkCoordZ * 16;

    glPushMatrix();

    glEnable(GL_BLEND);
    glDisable(GL_TEXTURE_2D);
    glDepthMask(false);
    glDisable(GL_DEPTH_TEST);

    glEnable(GL_LINE_SMOOTH);
    glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
    glLineWidth(2.5f);

    RenderUtils.color(HUDModule.primary.value().getRGB());

    glTranslated(-renderX, -renderY, -renderZ);

    glBegin(GL_LINES);
    {
      // vertical lines
      glVertex3d(minX, 0.0, minZ);
      glVertex3d(minX, 256.0, minZ);

      glVertex3d(minX + 16, 0.0, minZ);
      glVertex3d(minX + 16, 256.0, minZ);

      glVertex3d(minX, 0.0, minZ + 16);
      glVertex3d(minX, 256.0, minZ + 16);

      glVertex3d(minX + 16, 0.0, minZ + 16);
      glVertex3d(minX + 16, 256.0, minZ + 16);

      // horizontal lines (bottom)
      glVertex3d(minX, 0.0, minZ);
      glVertex3d(minX + 16, 0.0, minZ);

      glVertex3d(minX, 0.0, minZ + 16);
      glVertex3d(minX + 16, 0.0, minZ + 16);

      glVertex3d(minX, 0.0, minZ);
      glVertex3d(minX, 0.0, minZ + 16);

      glVertex3d(minX + 16, 0.0, minZ + 16);
      glVertex3d(minX + 16, 0.0, minZ);

      // horizontal lines (top)
      glVertex3d(minX, 256.0, minZ);
      glVertex3d(minX + 16, 256.0, minZ);

      glVertex3d(minX, 256.0, minZ + 16);
      glVertex3d(minX + 16, 256.0, minZ + 16);

      glVertex3d(minX, 256.0, minZ);
      glVertex3d(minX, 256.0, minZ + 16);

      glVertex3d(minX + 16, 256.0, minZ + 16);
      glVertex3d(minX + 16, 256.0, minZ);
    }
    glEnd();

    glLineWidth(1.0f);
    glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
    glDisable(GL_LINE_SMOOTH);

    glEnable(GL_DEPTH_TEST);
    glDepthMask(true);
    glEnable(GL_TEXTURE_2D);
    glDisable(GL_BLEND);

    glPopMatrix();
  };
}
