package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.render.EventRender3D;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.active.Colors;
import wtf.nebula.client.utils.render.RenderEngine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Trails extends ToggleableModule {
    private final Property<Boolean> pearls = new Property<>(true, "Pearls", "enderpearls");
    private final Property<Boolean> arrows = new Property<>(true, "Arrows");
    private final Property<Integer> lifetime = new Property<>(2, 1, 50, "Lifetime", "time");
    private final Property<Float> lineWidth = new Property<>(1.0f, 0.1f, 5.0f, "Line Width", "width");

    private final Map<Integer, List<Trail>> trailMap = new ConcurrentHashMap<>();

    public Trails() {
        super("Trails", new String[]{"throwntrails"}, ModuleCategory.VISUALS);
        offerProperties(pearls, arrows, lifetime, lineWidth);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        trailMap.clear();
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onRenderWorld(EventRender3D event) {
        for (Entity entity : mc.theWorld.loadedEntityList) {

            if ((entity instanceof EntityEnderPearl && pearls.getValue()) || (entity instanceof EntityArrow && arrows.getValue())) {

                if (!trailMap.containsKey(entity.getEntityId())) {
                    trailMap.put(entity.getEntityId(), new CopyOnWriteArrayList<>());
                }

                trailMap.get(entity.getEntityId()).add(new Trail(
                        new Vec3(Vec3.fakePool,
                                entity.lastTickPosX,
                                entity.lastTickPosY,
                                entity.lastTickPosZ),
                        System.currentTimeMillis()
                ));
            }
        }

        if (!trailMap.isEmpty()) {

            for (int id : trailMap.keySet()) {

                List<Trail> trails = trailMap.get(id);
                trails.removeIf((trail) -> System.currentTimeMillis() - trail.time >= lifetime.getValue() * 1000L);

                if (!trails.isEmpty()) {
                    glPushMatrix();
                    glEnable(GL_BLEND);
                    OpenGlHelper.glBlendFunc(771, 770, 0, 1);
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_DEPTH_TEST);

                    glEnable(GL_LINE_SMOOTH);
                    glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
                    glLineWidth(lineWidth.getValue());

                    glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

                    RenderEngine.color(Colors.rainbow.getValue() ? Colors.getClientRainbow(0) : Colors.getClientColor());

                    glBegin(GL_LINE_STRIP);

                    for (Trail trail : trails) {
                        glVertex3d(trail.vec.xCoord, trail.vec.yCoord, trail.vec.zCoord);
                    }

                    glEnd();

                    glLineWidth(1.0f);
                    glDisable(GL_LINE_SMOOTH);
                    glEnable(GL_DEPTH_TEST);
                    glEnable(GL_TEXTURE_2D);
                    glDisable(GL_BLEND);
                    glPopMatrix();
                } else {
                    trailMap.remove(id);
                }
            }
        }
    }

    private static class Trail {
        private final Vec3 vec;
        private final long time;

        public Trail(Vec3 vec, long time) {
            this.vec = vec;
            this.time = time;
        }
    }
}
