package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.render.EventRender3D;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.active.Colors;
import wtf.nebula.client.utils.client.MathUtils;
import wtf.nebula.client.utils.render.RenderEngine;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Trajectories extends ToggleableModule {
    private final Property<Boolean> rotate = new Property<>(true, "Rotate", "rot");
    private final Property<Boolean> lines = new Property<>(true, "Lines", "lns");
    private final Property<Float> lineWidth = new Property<>(3.0f, 1.0f, 10.0f, "Line Width", "linewidth", "width")
            .setVisibility(lines::getValue);

    public Trajectories() {
        super("Trajectories", new String[]{"landingpath"}, ModuleCategory.VISUALS);
        offerProperties(rotate, lines, lineWidth);
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onRenderWorld(EventRender3D event) {
        HitResult hitResult = calculateTrajectory(mc.thePlayer, event.getPartialTicks());
        if (hitResult != null) {
            glPushMatrix();
            glEnable(GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 0, 1);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);

            RenderEngine.color(Colors.getClientColor(80));

            if (!hitResult.positions.isEmpty() && lines.getValue()) {
                glEnable(GL_LINE_SMOOTH);
                glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

                glLineWidth(lineWidth.getValue());

                glBegin(GL_LINE_STRIP);
                {
                    for (Vec3 v : hitResult.positions) {
                        glVertex3d(v.xCoord - RenderManager.renderPosX, v.yCoord - RenderManager.renderPosY, v.zCoord - RenderManager.renderPosZ);
                    }
                }
                glEnd();

                glLineWidth(1.0f);
                glDisable(GL_LINE_SMOOTH);
            }

            glTranslated(hitResult.x - RenderManager.renderPosX, hitResult.y - RenderManager.renderPosY, hitResult.z - RenderManager.renderPosZ);

            AxisAlignedBB box = new AxisAlignedBB(-0.5, -0.01, -0.5, 0.5, 0.01, 0.5);
            if (hitResult.result != null) {
                if (hitResult.result.entityHit == null) {
                    box = new AxisAlignedBB(-0.5, -0.01, -0.5, 0.5, 0.01, 0.5);

                    if (rotate.getValue()) {
                        int side = hitResult.result.sideHit;
                        if (side == 2 || side == 3) {
                            glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                        } else if (side == 4 || side == 5) {
                            glRotatef(-90.0f, 0.0f, 0.0f, -1.0f);
                        }
                    }
                } else {
                    box = hitResult.result.entityHit.boundingBox.copy();
                }
            }

            Tessellator tessellator = Tessellator.instance;

            tessellator.startDrawing(GL_QUADS);
            tessellator.addVertex(box.minX, box.minY, box.minZ);
            tessellator.addVertex(box.maxX, box.minY, box.minZ);
            tessellator.addVertex(box.maxX, box.minY, box.maxZ);
            tessellator.addVertex(box.minX, box.minY, box.maxZ);
            tessellator.draw();

            // sides
            tessellator.startDrawing(GL_QUADS);
            tessellator.addVertex(box.minX, box.minY, box.minZ);
            tessellator.addVertex(box.minX, box.minY, box.maxZ);
            tessellator.addVertex(box.minX, box.maxY, box.maxZ);
            tessellator.addVertex(box.minX, box.maxY, box.minZ);
            tessellator.draw();

            tessellator.startDrawing(GL_QUADS);
            tessellator.addVertex(box.maxX, box.minY, box.maxZ);
            tessellator.addVertex(box.maxX, box.minY, box.minZ);
            tessellator.addVertex(box.maxX, box.maxY, box.minZ);
            tessellator.addVertex(box.maxX, box.maxY, box.maxZ);
            tessellator.draw();

            tessellator.startDrawing(GL_QUADS);
            tessellator.addVertex(box.maxX, box.minY, box.minZ);
            tessellator.addVertex(box.minX, box.minY, box.minZ);
            tessellator.addVertex(box.minX, box.maxY, box.minZ);
            tessellator.addVertex(box.maxX, box.maxY, box.minZ);
            tessellator.draw();

            tessellator.startDrawing(GL_QUADS);
            tessellator.addVertex(box.minX, box.minY, box.maxZ);
            tessellator.addVertex(box.maxX, box.minY, box.maxZ);
            tessellator.addVertex(box.maxX, box.maxY, box.maxZ);
            tessellator.addVertex(box.minX, box.maxY, box.maxZ);
            tessellator.draw();

            // top
            tessellator.startDrawing(GL_QUADS);
            tessellator.addVertex(box.minX, box.maxY, box.maxZ);
            tessellator.addVertex(box.maxX, box.maxY, box.maxZ);
            tessellator.addVertex(box.maxX, box.maxY, box.minZ);
            tessellator.addVertex(box.minX, box.maxY, box.minZ);
            tessellator.draw();

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glPopMatrix();
        }
    }

    public static HitResult calculateTrajectory(EntityPlayer e, float partialTicks) {
        if (e == null) {
            return null;
        }

        ItemStack stack;
        if (e.equals(mc.thePlayer)) {
            stack = Nebula.getInstance().getInventoryManager().getHeld();
        } else {
            stack = e.getHeldItem();
        }

        if (stack == null || stack.getItem() == null) {
            return null;
        }

        if (!(stack.getItem() instanceof ItemBow || (stack.getItem() instanceof ItemEnderPearl) || stack.getItem() instanceof ItemSnowball || stack.getItem() instanceof ItemEgg || stack.getItem() instanceof ItemExpBottle || (stack.getItem() instanceof ItemPotion && ItemPotion.isSplash(stack.getItemDamage())))) {
            return null;
        }

        float yaw, pitch;
        if (e.equals(mc.thePlayer)) {
            float[] server = Nebula.getInstance().getRotationManager().getServerRotation();
            if (server == null) {
                server = new float[] { mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch };
            }

            yaw = server[0];
            pitch = server[1];
        } else {
            yaw = e.rotationYaw;
            pitch = e.rotationPitch;
        }

        double x = MathUtils.interpolate(e.posX, e.lastTickPosX, partialTicks);
        double y = MathUtils.interpolate(e.posY, e.lastTickPosY, partialTicks) - 0.10000000149011612;
        double z = MathUtils.interpolate(e.posZ, e.lastTickPosZ, partialTicks);

        float velocity = 0.0f;
        float pitchOffset = 0.0f;

        if (stack.getItem() instanceof ItemEnderPearl || stack.getItem() instanceof ItemEgg || stack.getItem() instanceof ItemSnowball) {
            velocity = 1.5f;
        } else if (stack.getItem() instanceof ItemExpBottle) {
            velocity = 0.7f;
            pitchOffset = -20.0f;
        } else if (stack.getItem() instanceof ItemPotion) {
            velocity = 0.5f;
            pitchOffset = -20.0f;
        } else if (stack.getItem() instanceof ItemBow) {
            int charge = stack.getMaxItemUseDuration() - e.getItemInUseCount();
            velocity = getArrowVelocity(charge) * 3.0f;
        }

        float magicNumber = 0.017453292f;

        double motionX = -MathHelper.sin(yaw * magicNumber) * MathHelper.cos(pitch * magicNumber);
        double motionY = -MathHelper.sin((pitch + pitchOffset) * magicNumber);
        double motionZ = MathHelper.cos(yaw * magicNumber) * MathHelper.cos(pitch * magicNumber);

        double distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);

        motionX /= distance;
        motionY /= distance;
        motionZ /= distance;

        motionX *= velocity;
        motionY *= velocity;
        motionZ *= velocity;

        boolean landed = false;

        double size = stack.getItem() instanceof ItemBow ? 0.3 : 0.25;
        double lastDist = 0.0;

        List<Vec3> positions = new ArrayList<>();
        MovingObjectPosition finalResult = null;

        while (!landed && y > 0.0) {
            Vec3 pos = new Vec3(Vec3.fakePool, x, y, z);
            Vec3 motion = new Vec3(Vec3.fakePool, x + motionX, y + motionY, z + motionZ);

            MovingObjectPosition result = mc.theWorld.rayTraceBlocks(pos, motion);
            if (result != null && !result.typeOfHit.equals(MovingObjectPosition.MovingObjectType.MISS)) {
                landed = true;
                finalResult = result;
            }

            AxisAlignedBB bb = new AxisAlignedBB(
                    x - size, y - size, z - size,
                    x + size, y + size, z + size)
                    .addCoord(motionX, motionY, motionZ)
                    .expand(1.0, 1.0, 1.0);

            List<Entity> entitiesColliding = mc.theWorld.getEntitiesWithinAABB(Entity.class, bb);
            if (!entitiesColliding.isEmpty()) {
                for (Entity entity : entitiesColliding) {
                    AxisAlignedBB box = entity.boundingBox;
                    if (!entity.canBeCollidedWith() || entity.equals(mc.thePlayer) || entity.getEntityId() == -133769420) {
                        continue;
                    }

                    box = box.copy().expand(0.3, 0.3, 0.3);

                    MovingObjectPosition r = box.calculateIntercept(pos, motion);
                    if (r != null) {
                        double dist = pos.distanceTo(r.hitVec);
                        if (dist < lastDist || lastDist == 0.0) {
                            lastDist = dist;

                            landed = true;
                            finalResult = r;
                        }
                    }
                }
            }

            x += motionX;
            y += motionY;
            z += motionZ;

            motionX *= 0.99;
            motionY *= 0.99;
            motionZ *= 0.99;

            if (stack.getItem() instanceof ItemExpBottle) {
                motionY -= 0.07;
            } else if (stack.getItem() instanceof ItemBow || stack.getItem() instanceof ItemPotion) {
                motionY -= 0.05;
            } else {
                motionY -= 0.03;
            }

            positions.add(new Vec3(Vec3.fakePool, x, y, z));
        }

        return new HitResult(x, y, z, finalResult, positions);
    }

    // from 1.12.2 decompiled MCP
    public static float getArrowVelocity(int charge) {
        float f = (float) charge / 20.0f;
        f = (f * f + f * 2.0f) / 3.0f;

        if (f > 1.0f) {
            f = 1.0f;
        }

        return f;
    }

    public static class HitResult {
        private final double x, y, z;
        private final MovingObjectPosition result;
        private final List<Vec3> positions;

        public HitResult(double x, double y, double z, MovingObjectPosition result, List<Vec3> positions) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.result = result;
            this.positions = positions;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public MovingObjectPosition getResult() {
            return result;
        }

        public List<Vec3> getPositions() {
            return positions;
        }
    }
}
