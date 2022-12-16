package wtf.nebula.client.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.event.impl.render.EventRender3D;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.active.Colors;
import wtf.nebula.client.impl.module.visuals.LogoutSpots;
import wtf.nebula.client.impl.module.world.FreeCamera;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.player.RotationUtils;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.world.DamageUtils;
import wtf.nebula.client.utils.world.WorldUtils;

import java.util.ArrayList;
import java.util.Comparator;

import static org.lwjgl.opengl.GL11.*;

public class AutoBed extends ToggleableModule {
    private final Property<Double> speed = new Property<>(15.0, 0.1, 20.0, "Speed", "s");
    private final Property<Double> range = new Property<>(4.0, 1.0, 6.0, "Range", "dist", "distance");
    private final Property<Double> wallRange = new Property<>(3.0, 1.0, 6.0, "Wall Range", "walldist", "walldistance");
    private final Property<Boolean> swing = new Property<>(true, "Swing", "swingarm");
    private final Property<Rotate> rotate = new Property<>(Rotate.NORMAL, "Rotate", "rot", "face");
    private final Property<Swap> swap = new Property<>(Swap.CLIENT, "Swap", "switch");
    private final Property<Float> minDamage = new Property<>(5.0f, 0.0f, 20.0f, "Min Damage", "minimumdamage", "mindmg");
    private final Property<Float> maxLocal = new Property<>(10.0f, 0.0f, 20.0f, "Max Local", "maxlocal", "maxlocaldamage");
    private final Property<Float> minHealth = new Property<>(10.0f, 0.0f, 20.0f, "Min Health", "minimumhealth", "minhealth");
    private final Property<Boolean> suicide = new Property<>(false, "Suicide");
    private final Property<Boolean> protectFriends = new Property<>(true, "Protect Friends", "protectfriends");
    private final Property<Boolean> instant = new Property<>(false, "Instant", "packet");
    private final Property<Boolean> render = new Property<>(true, "Render", "draw");

    private final Timer timer = new Timer();
    private EntityPlayer target = null;
    private PlaceInfo info = null;
    private float damage = 0.0f;

    private boolean swapped = false;

    public AutoBed() {
        super("Auto Bed", new String[]{"autobed", "bedaura"}, ModuleCategory.COMBAT);
        offerProperties(speed, range, wallRange, swing, rotate, swap, minDamage, maxLocal, minHealth, suicide, protectFriends, instant, render);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        if (!isNull() && swapped) {
            swapBack();
        }

        swapped = false;
        info = null;
        target = null;
        damage = 0.0f;
    }

    @EventListener
    public void onRender3D(EventRender3D even) {
        if (info != null && render.getValue()) {
            AxisAlignedBB bb = new AxisAlignedBB(info.vec3.xCoord, info.vec3.yCoord, info.vec3.zCoord,
                    info.vec3.xCoord + 1.0, info.vec3.yCoord + 0.5, info.vec3.zCoord + 1.0)
                    .addCoord(info.facing.getFrontOffsetX(), info.facing.getFrontOffsetY(), info.facing.getFrontOffsetZ());

            AxisAlignedBB box = bb.copy().offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

            glPushMatrix();
            glEnable(GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 0, 1);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);
            //glDisable(GL_LIGHTING);

            RenderEngine.color(Colors.getClientColor(77));

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

            //glEnable(GL_LIGHTING);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glPopMatrix();

            Vec3 centered = bb.getCenter().addVector(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

            double x = centered.xCoord;
            double y = centered.yCoord;
            double z = centered.zCoord;

            double renderX = RenderManager.renderPosX;
            double renderY = RenderManager.renderPosY;
            double renderZ = RenderManager.renderPosZ;

            double distance = mc.renderViewEntity.getDistance(x + renderX, y + renderY, z + renderZ);
            double scale = (0.3 * Math.max(distance, 4.0)) / 50.0;

            glPushMatrix();

            RenderHelper.enableGUIStandardItemLighting();
            glDisable(GL_LIGHTING);

            glEnable(GL_POLYGON_OFFSET_FILL);
            glPolygonOffset(1.0f, -1500000.0f);

            //glEnable(GL_RESCALE_NORMAL);

            glTranslated(x, y, z);
            glRotated(-RenderManager.instance.playerViewY, 0.0f, 1.0f, 0.0f);
            glRotated(RenderManager.instance.playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
            glScaled(-scale, -scale, scale);

            glDisable(GL_DEPTH_TEST);

            String text = String.format("%.2f", damage);
            int width = mc.fontRenderer.getStringWidth(text) / 2;

            mc.fontRenderer.drawStringWithShadow(text, (int) -(width - 4.0), -(mc.fontRenderer.FONT_HEIGHT - 2), -1);

            glPolygonOffset(1.0f, 1500000.0f);
            glDisable(GL_POLYGON_OFFSET_FILL);

            //glDisable(GL_RESCALE_NORMAL);

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_LIGHTING);

            RenderHelper.enableStandardItemLighting();

            glEnable(GL_ALPHA_TEST);

            glPopMatrix();
        }
    }

    @EventListener
    public void onTick(EventTick event) {
        target = new ArrayList<>(mc.theWorld.playerEntities)
                .stream()
                .filter(this::isValidTarget)
                .min(Comparator.comparingDouble((e) -> mc.thePlayer.getDistanceSqToEntity(e)))
                .orElse(null);

        if (target == null) {
            return;
        }

        if (!PlayerUtils.isUnkillable(mc.thePlayer) && mc.thePlayer.getHealth() < minHealth.getValue() && !suicide.getValue()) {

            swapBack();

            return;
        }

        int slot = getBedSlot();
        if (slot == -1) {
            return;
        }

        info = getNextPlace();
        if (info == null) {
            swapBack();
            return;
        }

        if (!mc.theWorld.provider.isHellWorld) {
            swapBack();
            return;
        }

        if (rotate.getValue().equals(Rotate.NORMAL)) {
            Vec3 rotate = info.vec3
                    .addVector(0.5, 0.5, 0.5)
                    .addVector(
                            (double) info.facing.getFrontOffsetX() * 0.5,
                            (double) info.facing.getFrontOffsetY() * 0.5,
                            (double) info.facing.getFrontOffsetZ() * 0.5);

            Nebula.getInstance().getRotationManager().setRotations(RotationUtils.calcAngles(rotate));
        } else if (rotate.getValue().equals(Rotate.DIRECTIONAL)) {
            float yaw = MathHelper.wrapAngleTo180_float(info.facing.hIndex * 90.0f);
            Nebula.getInstance().getRotationManager().setRotations(new float[] { yaw, mc.thePlayer.rotationPitch });
        }

        if (timer.getTimePassedMs() / 50.0 >= 20.0 - speed.getValue()) {

            if (WorldUtils.isReplaceable(info.vec3)) {
                swapToBed(slot);

                boolean sneak = WorldUtils.SNEAK_BLOCKS.contains(WorldUtils.getBlock(info.vec3));
                if (sneak) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 1));
                }

                boolean result = mc.playerController.onPlayerRightClick(
                        mc.thePlayer,
                        mc.theWorld,
                        Nebula.getInstance().getInventoryManager().getHeld(),
                        (int) info.vec3.xCoord,
                        (int) info.vec3.yCoord - 1,
                        (int) info.vec3.zCoord,
                        EnumFacing.UP.order_a,
                        info.vec3.addVector(0.5, 0.5, 0.5)
                );

                if (result) {
                    timer.resetTime();

                    if (swing.getValue()) {
                        mc.thePlayer.swingItem();
                    } else {
                        mc.thePlayer.swingItemSilent();
                    }
                }

                if (sneak) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 2));
                }
            } else {


                //swapBack();
                boolean result = mc.playerController.onPlayerRightClick(
                        mc.thePlayer,
                        mc.theWorld,
                        Nebula.getInstance().getInventoryManager().getHeld(),
                        (int) info.vec3.xCoord,
                        (int) info.vec3.yCoord,
                        (int) info.vec3.zCoord,
                        EnumFacing.UP.order_a,
                        info.vec3.addVector(0.5, 0.5, 0.5)
                );

                if (result) {
                    timer.resetTime();

                    if (swing.getValue()) {
                        mc.thePlayer.swingItem();
                    } else {
                        mc.thePlayer.swingItemSilent();
                    }
                }
            }
        }
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getEra().equals(Era.PRE)) {

            if (instant.getValue()) {
                if (event.getPacket() instanceof S23PacketBlockChange) {
                    S23PacketBlockChange packet = event.getPacket();

                    if (packet.func_148880_c() != null && packet.func_148880_c().equals(Blocks.bed)) {

                        int x = packet.x;
                        int y = packet.y;
                        int z = packet.z;

                        if ((int) info.vec3.xCoord == x || (int) info.vec3.yCoord == y || (int) info.vec3.zCoord == z) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(x, y, z, EnumFacing.UP.order_a, Nebula.getInstance().getInventoryManager().getHeld(), 0.5f, 0.5f, 0.5f));

                            if (swing.getValue()) {
                                mc.thePlayer.swingItem();
                            } else {
                                mc.thePlayer.swingItemSilent();
                            }

                            int slot = getBedSlot();
                            if (slot != -1) {
                                swapToBed(slot);
                                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(x, y - 1, z, EnumFacing.UP.order_a, Nebula.getInstance().getInventoryManager().getHeld(), 0.5f, 0.5f, 0.5f));

                                if (swing.getValue()) {
                                    mc.thePlayer.swingItem();
                                } else {
                                    mc.thePlayer.swingItemSilent();
                                }

                                timer.resetTime();
                            }
                        }
                    }
                }
            }
        }
    }

    private int getBedSlot() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemBed) {
                slot = i;
            }
        }

        return slot;
    }

    private void swapToBed(int slot) {
        switch (swap.getValue()) {
            case NONE: {
                if (mc.thePlayer.inventory.currentItem != slot) {
                    return;
                }

                break;
            }

            case CLIENT: {
                mc.thePlayer.inventory.currentItem = slot;
                break;
            }

            case SERVER: {
                if (Nebula.getInstance().getInventoryManager().serverSlot != slot) {
                    swapped = true;
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                }
                break;
            }
        }
    }

    private void swapBack() {
        if (swap.getValue().equals(Swap.SERVER) && swapped) {
            mc.thePlayer.sendQueue.addToSendQueueSilent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }

        swapped = false;
    }

    public PlaceInfo getNextPlace() {
        PlaceInfo i = null;
        float dmg = minDamage.getValue();

        if (info != null && !isBlocking(info.vec3, info.facing)) {
            i = info;
            dmg = damage;

            if (damage < minDamage.getValue()) {


                float calc = damageAtForCurrentTarget(info.vec3);
                if (calc == -1.0f) {
                    i = info = null;
                    dmg = damage = minDamage.getValue();
                } else {
                    dmg = damage = calc;
                }
            }
        }

        int r = range.getValue().intValue();

        for (int x = -r; x <= r; ++x) {
            for (int y = -r; y <= r; ++y) {
                for (int z = -r; z <= r; ++z) {
                    int posY = (int) Math.floor(target.boundingBox.minY) + y;
                    Vec3 pos = new Vec3(Vec3.fakePool, Math.floor(mc.thePlayer.posX), posY, Math.floor(mc.thePlayer.posZ)).addVector(x, y, z);

                    EnumFacing facing = getPlaceFace(pos);
                    if (facing != null) {
                        float d = damageAtForCurrentTarget(pos);
                        if (d == -1.0f) {
                            continue;
                        }

                        if (d > dmg) {
                            i = new PlaceInfo(pos, facing);
                            dmg = d;
                        }
                    }
                }
            }
        }

        this.damage = dmg;
        return i;
    }

    private boolean isBlocking(Vec3 vec, EnumFacing facing) {
        Block block = WorldUtils.getBlock(vec);
        if (block.equals(Blocks.lava) || block.equals(Blocks.flowing_lava) || block.equals(Blocks.fire)) {
            return true;
        }

        block = WorldUtils.getBlock(vec.offset(facing));
        return block.equals(Blocks.lava) || block.equals(Blocks.flowing_lava) || block.equals(Blocks.fire);
    }

    private EnumFacing getPlaceFace(Vec3 vec) {

        if (!WorldUtils.isAir(vec)) {
            return null;
        }

        EnumFacing f = null;
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing.equals(EnumFacing.UP) || facing.equals(EnumFacing.DOWN)) {
                continue;
            }

            if (rotate.getValue().equals(Rotate.NORMAL) && !canPlaceWithFace(facing)) {
                continue;
            }

            Vec3 offset = vec.offset(facing);
            if (WorldUtils.isAir(offset)) {
                f = facing;
                break;
            }
        }

        // todo: better
        if (f != null) {
            if (!WorldUtils.isAir(vec.offset(f))) {
                return null;
            }

            if (WorldUtils.isReplaceable(vec.offset(EnumFacing.DOWN))) {
                return null;
            }

            if (WorldUtils.isReplaceable(vec.offset(EnumFacing.DOWN).offset(f))) {
                return null;
            }


        }

        return f;
    }

    private float damageAtForCurrentTarget(Vec3 vec) {
        Vec3 pos = vec.addVector(0.5, 0.5, 0.5);
        float selfDmg = DamageUtils.calcDamage(mc.thePlayer, pos, 5.0f);

        if (!suicide.getValue()) {
            if (selfDmg >= mc.thePlayer.getHealth() || selfDmg > maxLocal.getValue()) {
                return -1.0f;
            }
        }

        float dmg = DamageUtils.calcDamage(target, pos, 5.0f);
        if (dmg < minDamage.getValue()) {
            return -1.0f;
        }

        if (!suicide.getValue() && selfDmg > dmg) {
            return -1.0f;
        }

        if (protectFriends.getValue()) {
            for (EntityPlayer player : new ArrayList<>(mc.theWorld.playerEntities)) {

                if (!Nebula.getInstance().getFriendManager().isFriend(player)) {
                    continue;
                }

                if (PlayerUtils.isUnkillable(player)) {
                    continue;
                }

                if (player.getDistanceSq(vec.xCoord, vec.yCoord, vec.zCoord) > range.getValue() * range.getValue()) {
                    continue;
                }

                float damage = DamageUtils.calcDamage(target, pos, 5.0f);
                if (damage > dmg || dmg > player.getHealth()) {
                    return -1.0f;
                }
            }
        }

        return dmg;
    }

    private boolean isValidTarget(EntityPlayer e) {
        if (e == null || !e.isEntityAlive() || e.equals(mc.thePlayer)) {
            return false;
        }

        if (LogoutSpots.isFake(e.getEntityId())) {
            return false;
        }

        if (Nebula.getInstance().getFriendManager().isFriend(e)) {
            return false;
        }

        // don't attack our camera guy
        if (Nebula.getInstance().getModuleManager().getModule(FreeCamera.class).isRunning() && e.getEntityId() == -133769420) {
            return false;
        }

        double dist = mc.thePlayer.getDistanceSqToEntity(e);
        double r = mc.thePlayer.canEntityBeSeen(e) ? range.getValue() : wallRange.getValue();

        return !(dist > r * r);
    }

    private boolean canPlaceWithFace(EnumFacing facing) {
        return PlayerUtils.getFacing().equals(facing);
    }

    public enum Swap {
        NONE, CLIENT, SERVER
    }

    public enum Rotate {
        NONE, NORMAL, DIRECTIONAL
    }

    public static class PlaceInfo {
        private final Vec3 vec3;
        private final EnumFacing facing;

        public PlaceInfo(Vec3 vec3, EnumFacing facing) {
            this.vec3 = vec3;
            this.facing = facing;
        }
    }
}
