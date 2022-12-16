package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.render.EventRender3D;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.world.FreeCamera;
import wtf.nebula.client.utils.client.MathUtils;
import wtf.nebula.client.utils.world.EntityUtils;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

// literally should've been one of the first ever render modules in this client
public class Tracers extends ToggleableModule {
    private final Property<Boolean> players = new Property<>(true, "Players", "plrs");
    private final Property<Boolean> mobs = new Property<>(true, "Mobs", "hostile");
    private final Property<Boolean> passive = new Property<>(true, "Passive", "nice", "animals", "ambient");
    private final Property<Float> lineWidth = new Property<>(1.5f, 0.1f, 5.0f, "Line Width", "linewidth");
    private final Property<Boolean> stem = new Property<>(true, "Stem", "lineup");
    private final Property<Target> target = new Property<>(Target.FEET, "Target", "goto");

    public Tracers() {
        super("Tracers", new String[]{"lines", "tracer"}, ModuleCategory.VISUALS);
        offerProperties(players, mobs, passive, lineWidth, stem, target);
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onRenderWorld(EventRender3D event) {
        for (Entity e : new ArrayList<>(mc.theWorld.loadedEntityList)) {
            if (!(e instanceof EntityLivingBase) || e.isDead || e.equals(mc.thePlayer)) {
                continue;
            }

            if (e == null || !e.isEntityAlive() || e.equals(mc.thePlayer)) {
                continue;
            }

            if (LogoutSpots.isFake(e.getEntityId())) {
                continue;
            }

            if (e instanceof EntityPigZombie) {
                continue;
            }

            // don't attack our camera guy
            if (Nebula.getInstance().getModuleManager().getModule(FreeCamera.class).isRunning() && e.getEntityId() == -133769420) {
                continue;
            }

            if (e instanceof EntityPlayer && !players.getValue()) {
                continue;
            }

            if (EntityUtils.isMob(e) && !mobs.getValue()) {
                continue;
            }

            if (EntityUtils.isPassive(e) && !passive.getValue()) {
                continue;
            }

            glPushMatrix();

            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);

            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glLineWidth(lineWidth.getValue());

            glDisable(GL_LIGHTING);

            if (e instanceof EntityPlayer && Nebula.getInstance().getFriendManager().isFriend((EntityPlayer) e)) {
                glColor3d(0.0, 1.0, 1.0);
            } else {
                double dist = mc.thePlayer.getDistanceToEntity(e) / 20.0;
                glColor3d(2.0 - dist, dist, 0.0);
            }

            glLoadIdentity();
            mc.entityRenderer.orientCamera(event.getPartialTicks());

            glBegin(GL_LINES);
            {
                double x = MathUtils.interpolate(e.posX, e.lastTickPosX, event.getPartialTicks());
                double y = MathUtils.interpolate(e.posY, e.lastTickPosY, event.getPartialTicks());
                double z = MathUtils.interpolate(e.posZ, e.lastTickPosZ, event.getPartialTicks());

                Vec3 eyes = mc.renderViewEntity.getLook(event.getPartialTicks());

                glVertex3d(eyes.xCoord, eyes.yCoord, eyes.zCoord);
                glVertex3d(x - RenderManager.renderPosX, (y + Target.getTarget(target.getValue(), e)) - RenderManager.renderPosY, z - RenderManager.renderPosZ);

                if (stem.getValue()) {
                    glVertex3d(x - RenderManager.renderPosX, y - RenderManager.renderPosY, z - RenderManager.renderPosZ);
                    glVertex3d(x - RenderManager.renderPosX, (y + (e.getEyeHeight() / 2.0f)) - RenderManager.renderPosY, z - RenderManager.renderPosZ);
                }
            }
            glEnd();

            glEnable(GL_LIGHTING);

            glLineWidth(1.0f);
            glDisable(GL_LINE_SMOOTH);

            glEnable(GL_TEXTURE_2D);
            glEnable(GL_DEPTH_TEST);

            glPopMatrix();
        }
    }

    public enum Target {
        HEAD, TORSO, LEGS, FEET;

        public static float getTarget(Target t, Entity e) {
            float eyeHeight = e.getEyeHeight();

            switch (t) {
                default:
                case HEAD:
                    return eyeHeight;

                case TORSO:
                    return (eyeHeight / 2.0f) + 0.5f;

                case LEGS:
                    return eyeHeight / 2.0f;

                case FEET:
                    return 0.0f;
            }
        }
    }
}
