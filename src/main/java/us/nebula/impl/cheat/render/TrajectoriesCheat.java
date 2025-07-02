package us.nebula.impl.cheat.render;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.render.EventRender3D;

import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 03/02/25
 */
@SuppressWarnings("unchecked")
@CheatManifest(name = "Trajectories",
        description = "Renders the projected path of a projectile",
        category = CheatCategory.RENDER)
public final class TrajectoriesCheat extends Cheat
{
    private static final float PI_180 = (float) Math.PI / 180.0f;

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        final TrajectoryResult result = calculateTrajectory(MC.thePlayer, event.getPartialTicks());
        if (result == null)
        {
            return;
        }

        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(2.5f);

        glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

        glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        glBegin(GL_LINE_STRIP);
        {
            for (final Vec3 vec3 : result.getTrail())
            {
                glVertex3d(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            }
        }
        glEnd();

        final Vec3 hitVec = result.getTrail().get(result.getTrail().size() - 1);

        glBegin(GL_LINE_STRIP);
        {
            for (double angle = 0.0; angle <= 360.0; angle += 0.5)
            {
                double rad = Math.toRadians(angle);
                glVertex3d(hitVec.xCoord + (Math.sin(rad) * 0.5),
                        hitVec.yCoord,
                        hitVec.zCoord - (Math.cos(rad) * 0.5));
            }
        }
        glEnd();

        final MovingObjectPosition landing = result.getLanding();
        if (landing != null)
        {

        }

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    };

    private TrajectoryResult calculateTrajectory(final EntityPlayer player,
                                                 final float partialTicks)
    {
        final ItemStack stack = player.getHeldItem();
        if (stack == null || !(stack.getItem() instanceof ItemBow
                || (stack.getItem() instanceof ItemEnderPearl)
                || stack.getItem() instanceof ItemSnowball
                || stack.getItem() instanceof ItemEgg
                || stack.getItem() instanceof ItemExpBottle
                || (stack.getItem() instanceof ItemPotion
                && ItemPotion.isSplash(stack.getItemDamage()))))
        {
            return null;
        }

        double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks - 0.10000000149011612;
        double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

        final float yaw = MC.thePlayer.rotationYaw;
        final float pitch = MC.thePlayer.rotationPitch;

        final double size = stack.getItem() instanceof ItemBow ? 0.3 : 0.25;

        float velocity = 0.0f;
        float pitchOffset = 0.0f;
        if (stack.getItem() instanceof ItemEnderPearl
                || stack.getItem() instanceof ItemEgg
                || stack.getItem() instanceof ItemSnowball) {
            velocity = 1.5f;
        } else if (stack.getItem() instanceof ItemExpBottle)
        {
            velocity = 0.7f;
            pitchOffset = -20.0f;
        } else if (stack.getItem() instanceof ItemPotion)
        {
            velocity = 0.5f;
            pitchOffset = -20.0f;
        } else if (stack.getItem() instanceof ItemBow)
        {
            int charge = stack.getMaxItemUseDuration() - player.getItemInUseCount();
            velocity = getArrowVelocity(charge) * 3.0f;
        }

        double motionX = -MathHelper.sin(yaw * PI_180) * MathHelper.cos(pitch * PI_180);
        double motionY = -MathHelper.sin((pitch + pitchOffset) * PI_180);
        double motionZ = MathHelper.cos(yaw * PI_180) * MathHelper.cos(pitch * PI_180);

        final double distance = MathHelper.sqrt_double(
                motionX * motionX
                        + motionY * motionY
                        + motionZ * motionZ);

        motionX /= distance;
        motionY /= distance;
        motionZ /= distance;

        motionX *= velocity;
        motionY *= velocity;
        motionZ *= velocity;

        boolean landed = false;
        double lastDist = 0.0;

        MovingObjectPosition finalResult = null;
        final TrajectoryResult trajectoryResult = new TrajectoryResult();

        while (!landed && y > 0.0)
        {
            final Vec3 pos = Vec3.createVectorHelper(x, y, z);
            final Vec3 motion = pos.addVector(motionX, motionY, motionZ);
            //Vec3.createVectorHelper(x + motionX, y + motionY, z + motionZ);

            final MovingObjectPosition result = MC.theWorld.rayTraceBlocks(pos, motion);
            if (result != null && !result.typeOfHit.equals(MovingObjectPosition.MovingObjectType.MISS))
            {
                landed = true;
                finalResult = result;
            }

            final List<Entity> entitiesColliding = MC.theWorld.getEntitiesWithinAABB(Entity.class,
                    new AxisAlignedBB(
                        x - size, y - size, z - size,
                        x + size, y + size, z + size)
                        .addCoord(motionX, motionY, motionZ)
                        .expand(1.0, 1.0, 1.0));
            if (!entitiesColliding.isEmpty())
            {
                for (final Entity entity : entitiesColliding)
                {
                    AxisAlignedBB aabb = entity.boundingBox;
                    if (!entity.canBeCollidedWith() || entity.equals(MC.thePlayer))
                    {
                        continue;
                    }
                    aabb = aabb.copy().expand(0.3, 0.3, 0.3);
                    final MovingObjectPosition interceptedRaytrace = aabb.calculateIntercept(pos, motion);
                    if (interceptedRaytrace != null)
                    {
                        // TODO: exempt foliage blocks as they don't hit with a projectile 
                        final double hitVecDistance = pos.distanceTo(interceptedRaytrace.hitVec);
                        if (hitVecDistance < lastDist || lastDist == 0.0)
                        {
                            lastDist = hitVecDistance;
                            landed = true;
                            finalResult = interceptedRaytrace;
                        }
                    }
                }
            }

            x += motionX;
            y += motionY;
            z += motionZ;

            trajectoryResult.addTrail(Vec3.createVectorHelper(x, y, z));

            motionX *= 0.99;
            motionY *= 0.99;
            motionZ *= 0.99;

            if (stack.getItem() instanceof ItemExpBottle)
            {
                motionY -= 0.07;
            } else if (stack.getItem() instanceof ItemBow
                    || stack.getItem() instanceof ItemPotion)
            {
                motionY -= 0.05;
            } else
            {
                motionY -= 0.03;
            }
        }

        trajectoryResult.setLanding(finalResult);
        return trajectoryResult;
    }

    private float getArrowVelocity(final int charge) {
        float f = (float) charge / 20.0f;
        f = (f * f + f * 2.0f) / 3.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    private static class TrajectoryResult
    {
        private final List<Vec3> trail = new LinkedList<>();
        private MovingObjectPosition landing;

        public void addTrail(final Vec3 vec3)
        {
            trail.add(vec3);
        }

        public List<Vec3> getTrail()
        {
            return trail;
        }

        public MovingObjectPosition getLanding()
        {
            return landing;
        }

        public void setLanding(MovingObjectPosition landing)
        {
            this.landing = landing;
        }
    }
}
