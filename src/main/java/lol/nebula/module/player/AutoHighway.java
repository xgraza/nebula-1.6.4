package lol.nebula.module.player;

import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.move.EventMove;
import lol.nebula.listener.events.entity.move.EventWalkingUpdate;
import lol.nebula.listener.events.render.world.EventRender3D;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.module.combat.AutoGapple;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.RotationUtils;
import lol.nebula.util.player.MoveUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    private static final int BREAKING_COLOR = new Color(255, 0, 0, 120).getRGB();
    private static final int BREAK_COLOR = new Color(0, 255, 0, 120).getRGB();
    private static final int PLACE_COLOR = new Color(0, 0, 255, 120).getRGB();

    private final Setting<Boolean> autoWalk = new Setting<>(true, "Auto Walk");
    private final Setting<Boolean> rotate = new Setting<>(true, "Rotate");
    private final Setting<Integer> clearance = new Setting<>(1, 0, 4, "Clearance");

    private final Queue<Position> placePositions = new ConcurrentLinkedQueue<>();
    private final Queue<Position> breakPositions = new ConcurrentLinkedQueue<>();
    private Position breakPos;
    private boolean autoWalking;

    public AutoHighway() {
        super("Auto Highway", "Automatically builds highways", ModuleCategory.PLAYER);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        placePositions.clear();
        breakPositions.clear();
        breakPos = null;

        if (autoWalking) {
            mc.gameSettings.keyBindForward.pressed = false;
            autoWalking = false;
        }
    }

    @Listener
    public void onRender3D(EventRender3D event) {
        glPushMatrix();
        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        for (Position place : breakPositions) {
            if (place.equals(breakPos)) continue;

            setColor(BREAK_COLOR);
            filledAabb(new AxisAlignedBB(
                    place.getX(), place.getY(), place.getZ(),
                    place.getX() + 1, place.getY() + 1, place.getZ() + 1)
                    .offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ));
        }

        for (Position place : placePositions) {
            if (place.equals(breakPos)) continue;

            setColor(PLACE_COLOR);
            filledAabb(new AxisAlignedBB(
                    place.getX(), place.getY(), place.getZ(),
                    place.getX() + 1, place.getY() + 1, place.getZ() + 1)
                    .offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ));
        }

        if (breakPos != null) {
            setColor(BREAKING_COLOR);
            filledAabb(new AxisAlignedBB(
                    breakPos.getX(), breakPos.getY(), breakPos.getZ(),
                    breakPos.getX() + 1, breakPos.getY() + 1, breakPos.getZ() + 1)
                    .offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ));
        }

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {
        updatePositions();

        if (autoWalk.getValue()) {

            AutoGapple AUTO_GAPPLE = Nebula.getInstance().getModules().get(AutoGapple.class);

            // do not interfere with auto gapple / auto eat (TODO)
            if (AUTO_GAPPLE.isEating()) {
                autoWalking = false;
                mc.gameSettings.keyBindForward.pressed = false;
                return;
            }

            // there should be some better logic here but
            // if there are more than 2 "layers" of highway to place (7 blocks for one, * 2 for 14)
            autoWalking = placePositions.size() <= 7 * 2;
            mc.gameSettings.keyBindForward.pressed = autoWalking;

        }

        // if we are breaking something
        if (breakPos != null) {

            // block has been broken, reset
            if (isReplaceable(breakPos.getX(), breakPos.getY(), breakPos.getZ())) breakPos = null;

            // if we have not invalidated this break position, return
            if (breakPos != null) return;
        }

        // break blocks
        breakBlocks: {
            if (breakPositions.isEmpty()) break breakBlocks;

            Position next = breakPositions.poll();
            if (next == null) break breakBlocks;

            if (rotate.getValue()) RotationUtils.setRotations(event, RotationUtils.toBlock(
                    next.getX(), next.getY(), next.getZ(), EnumFacing.UP));

            breakBlock(next);

            return;
        }

        placeBlocks: {

            if (placePositions.isEmpty()) break placeBlocks;;

            Position next = placePositions.poll();
            if (next == null) break placeBlocks;

            place(next);

        }
    }

    @Listener
    public void onMove(EventMove event) {
        if (mc.thePlayer.onGround && autoWalk.getValue()) MoveUtils.safewalk(event);
    }

    private void breakBlock(Position next) {
        int slot = AutoTool.getBestSlotFor(mc.theWorld.getBlock(next.getX(), next.getY(), next.getZ()));
        if (slot != -1) mc.thePlayer.inventory.currentItem = slot;

        breakPos = next;

        // ncp moment
        mc.thePlayer.swingItemSilent();

        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(0, next.getX(), next.getY(), next.getZ(), EnumFacing.UP.getOrder_a()));
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(2, next.getX(), next.getY(), next.getZ(), EnumFacing.UP.getOrder_a()));
    }

    private void place(Position next) {
        int face = -1;
        for (EnumFacing facing : EnumFacing.values()) {
            Position n = next.add(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
            if (!isReplaceable(n.getX(), n.getY(), n.getZ())) {
                next = n;
                face = getOpposite(facing).getOrder_a();
                break;
            }
        }

        if (face == -1) return;

        Block lookingFor = Blocks.obsidian;
        if (mc.theWorld.getBlock(next.getX(), next.getY(), next.getZ()) instanceof BlockLiquid) {
            lookingFor = Blocks.netherrack;
        }

        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null || !(stack.getItem() instanceof ItemBlock)) continue;

            if (((ItemBlock) stack.getItem()).field_150939_a == lookingFor) {
                slot = i;
                break;
            }
        }

        if (slot == -1) return;
        mc.thePlayer.inventory.currentItem = slot;

        if (rotate.getValue()) Nebula.getInstance().getRotations().spoof(RotationUtils.toBlock(
                next.getX(), next.getY(), next.getZ(), EnumFacing.faceList[face]));

        boolean result = mc.playerController.onPlayerRightClick(mc.thePlayer,
                mc.theWorld,
                mc.thePlayer.getHeldItem(),
                next.getX(), next.getY(), next.getZ(), face,
                new Vec3(Vec3.fakePool, next.getX() + 0.5, next.getY() + 0.5, next.getZ() + 0.5));
        if (result) mc.thePlayer.swingItem();
    }

    private void updatePositions() {
        placePositions.removeIf(this::isInvalid);
        breakPositions.removeIf((x) -> isReplaceable(x.getX(), x.getY(), x.getZ()));

        List<Position> highwayPositions = getHighwayPositions();
        if (highwayPositions.isEmpty()) {
            placePositions.clear();
            breakPositions.clear();
            return;
        }

        for (Position vec : highwayPositions) {
            if (!placePositions.contains(vec) && !isInvalid(vec)) placePositions.add(vec);

            for (int i = 0; i < 3 + clearance.getValue(); ++i) {
                Position above = vec.add(0, i, 0);
                if (!isReplaceable(above.getX(), above.getY(), above.getZ())) {
                    Block at = mc.theWorld.getBlock(vec.getX(), vec.getY() + i, vec.getZ());
                    if (at == Blocks.air || at == Blocks.obsidian) continue;

                    if (at == Blocks.lava || at == Blocks.flowing_lava) {
                        placePositions.add(above);
                    } else {
                        if (!breakPositions.contains(above) && at.blockHardness != -1) breakPositions.add(above);
                    }
                }
            }
        }
    }

    private List<Position> getHighwayPositions() {
        List<Position> placeList = new ArrayList<>();
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
        for (double dist = 0.0; dist <= 3.0; dist += 1.0) {
            int x = (int) Math.floor((mc.thePlayer.posX + (0.5 * faceX)) + (faceX * dist));
            int y = (int) Math.floor(mc.thePlayer.boundingBox.minY) - 1;
            int z = (int) Math.floor((mc.thePlayer.posZ + (0.5 * faceZ)) + (faceZ * dist));

            if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
                placeList.add(new Position(x - 1, y, z));
                placeList.add(new Position(x, y, z));
                placeList.add(new Position(x + 1, y, z));
                placeList.add(new Position(x + 2, y, z));
                placeList.add(new Position(x - 2, y + 1, z));
                placeList.add(new Position(x + 3, y + 1, z));
            } else {
                placeList.add(new Position(x, y, z + 1));
                placeList.add(new Position(x, y, z));
                placeList.add(new Position(x, y, z - 1));
                placeList.add(new Position(x, y, z - 2));
                placeList.add(new Position(x, y + 1, z + 2));
                placeList.add(new Position(x, y + 1, z - 3));
            }
        }

        return placeList;
    }

    private boolean isInvalid(Position vec) {
        Block at = mc.theWorld.getBlock(vec.getX(), vec.getY(), vec.getZ());
        if (at == Blocks.obsidian || at.blockHardness == -1) return true;

        return mc.thePlayer.getDistanceSq(vec.getX(), vec.getY(), vec.getZ()) > 4 * 4;
    }

    private static class Position {
        private final int x, y, z;

        public Position(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public Position add(int x, int y, int z) {
            return new Position(this.x + x, this.y + y, this.z + z);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Position)) return false;
            Position pos = (Position) obj;
            return pos.getX() == x && pos.getY() == y && pos.getZ() == z;
        }
    }
}
