package wtf.nebula.impl.module.render;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import wtf.nebula.event.RenderWorldEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.MathUtil;

import static org.lwjgl.opengl.GL11.*;

public class Tracers extends Module {
    public Tracers() {
        super("Tracers", ModuleCategory.RENDER);
    }

    public final Value<Boolean> stem = new Value<>("Stem", true);

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {

        // for every entity in the world
        for (Object obj : mc.theWorld.loadedEntityList) {

            // it has to be an entity living base, if not fuck off
            if (!(obj instanceof EntityLivingBase)) {
                continue;
            }

            EntityLivingBase entity = (EntityLivingBase) obj;

            // we do not want to draw a line to ourselves or dead entities
            if (entity.equals(mc.thePlayer) || entity.isDead || entity.getHealth() <= 0.0f) {
                continue;
            }

            // push a new matrix stack
            glPushMatrix();

            // blending
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            // distance of the entity to us for distancing color
            float distance = (float) (mc.thePlayer.getDistanceToEntity(entity) / 20.0);
            glColor3f(2.0f - distance, distance, 0.0f);

            // disable texture & depth
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);

            // our tracer line width
            // TODO: setting?
            glLineWidth(1.5f);

            // our interpolated entity positions. this is to make the lines to the entities look smooth
            // we then offset by our camera render positions
            double x = MathUtil.interpolate(entity.posX, entity.lastTickPosX, event.getPartialTicks()) - RenderManager.renderPosX;
            double y = MathUtil.interpolate(entity.posY, entity.lastTickPosY, event.getPartialTicks()) - RenderManager.renderPosY;
            double z = MathUtil.interpolate(entity.posZ, entity.lastTickPosZ, event.getPartialTicks()) - RenderManager.renderPosZ;

            // smooth lines :sunglasses:
            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

            // begin drawing
            glBegin(GL_LINES);

                // draw from our crosshair to the entity
                glVertex3d(ActiveRenderInfo.objectX, ActiveRenderInfo.objectY, ActiveRenderInfo.objectZ);
                glVertex3d(x, y, z);

                // if we should draw a stem, start from the bottom vertex and go up to their height
                if (stem.getValue()) {
                    glVertex3d(x, y, z);
                    glVertex3d(x, y + entity.height, z);
                }

            // quit drawing
            glEnd();

            // no more line smooth
            glDisable(GL_LINE_SMOOTH);

            // other GL resets
            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_DEPTH_TEST);

            // reset our line width to default
            glLineWidth(1.0f);

            // pop this matrix stack
            glPopMatrix();
        }
    }
}
