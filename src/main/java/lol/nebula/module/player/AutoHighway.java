package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.move.EventWalkingUpdate;
import lol.nebula.listener.events.render.world.EventRender3D;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.world.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static lol.nebula.util.render.RenderUtils.filledAabb;
import static lol.nebula.util.render.RenderUtils.setColor;
import static lol.nebula.util.world.WorldUtils.getOpposite;
import static lol.nebula.util.world.WorldUtils.isReplaceable;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author aesthetical
 * @since 05/06/23
 */
public class AutoHighway extends Module {

    private final Setting<Boolean> autoBreak = new Setting<>(true, "Auto Break");
    private final Setting<Boolean> autoWalk = new Setting<>(true, "Auto Walk");

    private final List<Vec3> placePositions = new CopyOnWriteArrayList<>();
    private final List<Vec3> breakPositions = new CopyOnWriteArrayList<>();
    private Vec3 breakPos;

    public AutoHighway() {
        super("Auto Highway", "Automatically builds highways", ModuleCategory.PLAYER);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        placePositions.clear();
        breakPositions.clear();
        breakPos = null;
    }

    @Listener
    public void onRender3D(EventRender3D event) {
        //if (breakPos == null) return;

        glPushMatrix();
        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        for (Vec3 place : breakPositions) {
            setColor(0xFF00ff00);
            filledAabb(new AxisAlignedBB(
                    place.xCoord, place.yCoord, place.zCoord,
                    place.xCoord + 1, place.yCoord + 1, place.zCoord + 1)
                    .offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ));
        }

        for (Vec3 place : placePositions) {
            setColor(0xFF0000FF);
            filledAabb(new AxisAlignedBB(
                    place.xCoord, place.yCoord, place.zCoord,
                    place.xCoord + 1, place.yCoord + 1, place.zCoord + 1)
                    .offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ));
        }
//        setColor(0xFFFF0000);
//        filledAabb(new AxisAlignedBB(
//                breakPos.xCoord, breakPos.yCoord, breakPos.zCoord,
//                breakPos.xCoord + 1, breakPos.yCoord + 1, breakPos.zCoord + 1)
//                .offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ));

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {
        updatePositions();

        // some shit idk
    }

    private void breakBlock(Vec3 next) {
        int x = (int) next.xCoord;
        int y = (int) next.yCoord;
        int z = (int) next.zCoord;

        int slot = AutoTool.getBestSlotFor(mc.theWorld.getBlock(x, y, z));
        if (slot != -1) mc.thePlayer.inventory.currentItem = slot;

        breakPos = next;

        print("Breaking at " + next);

        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, EnumFacing.UP.getOrder_a()));
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, EnumFacing.UP.getOrder_a()));
    }

    private void place(Vec3 next) {
        int face = -1;
        for (EnumFacing facing : EnumFacing.values()) {
            Vec3 n = next.addVector(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
            if (!isReplaceable(n)) {
                next = n;
                face = getOpposite(facing).getOrder_a();
                break;
            }
        }

        if (face == -1) return;

        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null || !(stack.getItem() instanceof ItemBlock)) continue;

            if (((ItemBlock) stack.getItem()).field_150939_a == Blocks.obsidian) {
                slot = i;
                break;
            }
        }

        if (slot == -1) return;
        mc.thePlayer.inventory.currentItem = slot;

        boolean result = mc.playerController.onPlayerRightClick(mc.thePlayer,
                mc.theWorld,
                mc.thePlayer.getHeldItem(),
                (int) next.xCoord,
                (int) next.yCoord,
                (int) next.zCoord,
                face,
                next.addVector(0.5, 0.5, 0.5));
        if (result) mc.thePlayer.swingItem();
    }

    private void updatePositions() {
        placePositions.removeIf((x) -> isInvalid(x));
        breakPositions.removeIf(WorldUtils::isReplaceable);

        List<Vec3> highwayPositions = getHighwayPositions();
        if (highwayPositions.isEmpty()) {
            placePositions.clear();
            breakPositions.clear();
            return;
        }

        for (Vec3 vec : highwayPositions) {
            if (!placePositions.contains(vec) && !isInvalid(vec)) placePositions.add(vec);

            for (int i = 0; i < 3; ++i) {
                Vec3 above = vec.addVector(0, i, 0);
                if (!isReplaceable(above)) {
                    Block at = mc.theWorld.getBlock((int) vec.xCoord, (int) vec.yCoord + i, (int) vec.zCoord);
                    if (at == Blocks.air || at == Blocks.obsidian || at.blockHardness == -1) continue;
                    if (!breakPositions.contains(above)) breakPositions.add(above);
                }
            }
        }
    }

    private List<Vec3> getHighwayPositions() {
        List<Vec3> placeList = new ArrayList<>();
        int var25 = MathHelper.floor_double((double) (mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        EnumFacing facing = EnumFacing.valueOf(Direction.directions[var25]);

        int faceX = facing.getFrontOffsetX();
        int faceZ = facing.getFrontOffsetZ();

        // wtf lmao
        if (facing == EnumFacing.WEST) {
            faceX = -1;
        } else if (facing == EnumFacing.EAST) {
            faceX = 1;
        }

        // LOL THIS IS SO SHIT
        for (double dist = 0.0; dist <= mc.playerController.getBlockReachDistance(); dist += 1.0) {
            int x = (int) Math.floor((mc.thePlayer.posX + (0.5 * faceX)) + (faceX * dist));
            int y = (int) Math.floor(mc.thePlayer.boundingBox.minY) - 1;
            int z = (int) Math.floor((mc.thePlayer.posZ + (0.5 * faceZ)) + (faceZ * dist));

            if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
                placeList.add(new Vec3(Vec3.fakePool, x - 1, y, z));
                placeList.add(new Vec3(Vec3.fakePool, x, y, z));
                placeList.add(new Vec3(Vec3.fakePool, x + 1, y, z));
                placeList.add(new Vec3(Vec3.fakePool, x + 2, y, z));
                placeList.add(new Vec3(Vec3.fakePool, x - 2, y + 1, z));
                placeList.add(new Vec3(Vec3.fakePool, x + 3, y + 1, z));
            } else {
                placeList.add(new Vec3(Vec3.fakePool, x, y, z + 1));
                placeList.add(new Vec3(Vec3.fakePool, x, y, z));
                placeList.add(new Vec3(Vec3.fakePool, x, y, z - 1));
                placeList.add(new Vec3(Vec3.fakePool, x, y, z - 2));
                placeList.add(new Vec3(Vec3.fakePool, x, y + 1, z + 2));
                placeList.add(new Vec3(Vec3.fakePool, x, y + 1, z - 3));
            }
        }

        return placeList;
    }

    private boolean isInvalid(Vec3 vec) {
        Block at = mc.theWorld.getBlock((int) vec.xCoord, (int) vec.yCoord, (int) vec.zCoord);
        if (at == Blocks.obsidian || at.blockHardness == -1) return true;

        return mc.thePlayer.getDistanceSq(vec.xCoord, vec.yCoord, vec.zCoord) > 4 * 4;
    }
}
