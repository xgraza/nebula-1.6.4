package lol.nebula.module.world;

import com.google.common.collect.Lists;
import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.EventStage;
import lol.nebula.listener.events.entity.move.EventMove;
import lol.nebula.listener.events.entity.move.EventWalkingUpdate;
import lol.nebula.listener.events.render.gui.overlay.EventRender2D;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.Pair;
import lol.nebula.util.math.RotationUtils;
import lol.nebula.util.math.timing.Timer;
import lol.nebula.util.player.MoveUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.List;

import static lol.nebula.util.player.InventoryUtils.isInfinite;
import static lol.nebula.util.world.WorldUtils.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class Scaffold extends Module {

    /**
     * A list of all blocks to sneak on
     */
    private static final List<Block> blocksToSneak = Lists.newArrayList(
            Blocks.chest, Blocks.trapped_chest, Blocks.ender_chest,
            Blocks.beacon, Blocks.bed, Blocks.enchanting_table,
            Blocks.crafting_table, Blocks.furnace, Blocks.lit_furnace,
            Blocks.anvil, Blocks.command_block, Blocks.cake, Blocks.trapdoor,
            Blocks.wooden_door, Blocks.wooden_button, Blocks.stone_button,
            Blocks.fence_gate, Blocks.dragon_egg, Blocks.brewing_stand);

    private final Setting<Boolean> tower = new Setting<>(true, "Tower");
    private final Setting<Double> extend = new Setting<>(0.0, 0.5, 0.0, 6.0, "Extend");
    private final Setting<Boolean> rotate = new Setting<>(true, "Rotate");

    private final Setting<Boolean> blockCounter = new Setting<>(true, "Block Counter");

    private final Timer towerTimer = new Timer();

    // the previous rotations
    private float[] rotations;

    public Scaffold() {
        super("Scaffold", "Rapidly places blocks under your feet", ModuleCategory.WORLD);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        rotations = null;

        if (mc.thePlayer != null) {
            Nebula.getInstance().getInventory().sync();
        }
    }

    @Listener
    public void onRender2D(EventRender2D event) {
        if (!blockCounter.getValue()) return;

        int slot = getBlockSlot();
        if (slot == -1) return;

        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(slot);
        if (stack == null || !(stack.getItem() instanceof ItemBlock)) return;

        String countStr = "";
        if (isInfinite(stack)) {
            countStr = "Infinite";
        } else {
            int x = 0;
            for (int i = 0; i < 9; ++i) {
                ItemStack s = mc.thePlayer.inventory.getStackInSlot(i);
                if (s != null && s.getItem() instanceof ItemBlock) {
                    if (((ItemBlock) s.getItem()).field_150939_a.equals(((ItemBlock) stack.getItem()).field_150939_a)) {
                        x += s.stackSize;
                    }

                }
            }

            countStr = String.valueOf(x);
        }

        double x = event.getRes().getScaledWidth_double() / 2.0 + 5.0;
        double y = event.getRes().getScaledHeight_double() / 2.0 - (mc.fontRenderer.FONT_HEIGHT / 2.0);

        glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        RenderItem renderItem = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
        renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, (int) x, (int) y - 4);
        RenderHelper.disableStandardItemLighting();
        glPopMatrix();

        mc.fontRenderer.drawStringWithShadow(countStr + " blocks left", (int) (x + 17.0), (int) y, -1);
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {

        Pair<Vec3, EnumFacing> next = nextPlacePos();

        if (rotate.getValue()) {
            if (next != null) {

                double diffX = (next.getKey().xCoord + 0.5) - mc.thePlayer.posX;
                double diffZ = (next.getKey().zCoord + 0.5) - mc.thePlayer.posZ;

                rotations = new float[] {
                        (float) -(Math.toDegrees(Math.atan2(diffX, diffZ))),
                        (float) (94.0f - Math.random()) };
            }

            if (rotations != null) RotationUtils.setRotations(20, rotations);
        }

        // cannot place a null block
        if (next == null) return;

        int slot = getBlockSlot();
        if (slot == -1) return;

        if (event.getStage() == EventStage.POST) {

            // swap to the block slot
            Nebula.getInstance().getInventory().setSlot(slot);

            // sneak if we need to
            boolean sneakState = blocksToSneak.contains(getBlock(next.getKey())) && !mc.thePlayer.isSneaking();
            if (sneakState) mc.thePlayer.sendQueue.addToSendQueue(
                    new C0BPacketEntityAction(mc.thePlayer, 1));

            boolean result = mc.playerController.onPlayerRightClick(mc.thePlayer,
                    mc.theWorld,
                    mc.thePlayer.inventory.getStackInSlot(slot),
                    (int) next.getKey().xCoord,
                    (int) next.getKey().yCoord,
                    (int) next.getKey().zCoord,
                    next.getValue().getOrder_a(),
                    getHitVec(next.getKey(), next.getValue()));

            Nebula.getInstance().getInventory().sync();

            // un-sneak
            if (sneakState) mc.thePlayer.sendQueue.addToSendQueue(
                    new C0BPacketEntityAction(mc.thePlayer, 2));

            // don't continue if we failed to place
            if (!result) return;

            // silently swing the player's arm server-sided
            mc.thePlayer.swingItem();

            if (tower.getValue() && mc.gameSettings.keyBindJump.pressed) {
                if (mc.thePlayer.onGround && mc.thePlayer.motionY < 0.1) {
                    mc.thePlayer.motionY = 0.41999998688697815;
                } else if (mc.thePlayer.motionY <= 0.16477328182606651) {
                    mc.thePlayer.motionY = 0.41999998688697815;
                }

                if (towerTimer.ticks(6, true)) {
                    mc.thePlayer.motionX *= 0.3;
                    mc.thePlayer.motionZ *= 0.3;
                }
            }
        }
    }

    @Listener
    public void onMove(EventMove event) {
        if (mc.thePlayer.onGround
                && MoveUtils.isMoving()
                && !mc.gameSettings.keyBindJump.pressed) MoveUtils.safewalk(event);
    }

    /**
     * Gets the next block slot
     * @return the slot 0-8 or -1 if no blocks applied
     */
    private int getBlockSlot() {
        int slot = -1, count = 0;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null || !(stack.getItem() instanceof ItemBlock) || stack.stackSize == 0) continue;

            // automatically pick infinite items
            if (isInfinite(stack)) return i;

            if (stack.stackSize > count) {
                count = stack.stackSize;
                slot = i;
            }
        }

        return slot;
    }

    /**
     * Gets the next placement position
     * @return the next placement Vec3 or null
     */
    private Pair<Vec3, EnumFacing> nextPlacePos() {
        Vec3 pos = mc.thePlayer.getGroundPosition().addVector(0, -1, 0);

        if (extend.getValue() > 0.0 && !mc.gameSettings.keyBindJump.pressed) {
            // EntityLivingBase#jump(), i do not take movement into account (soon maybe?)
            float yaw = mc.thePlayer.rotationYaw * 0.017453292f;

            double distance = 0.0;
            while (distance <= extend.getValue()) {
                distance += extend.getScale().doubleValue();

                Vec3 extend = new Vec3(Vec3.fakePool,
                        Math.floor(mc.thePlayer.posX) + -Math.sin(yaw) * distance,
                        pos.yCoord,
                        Math.floor(mc.thePlayer.posZ) + Math.cos(yaw) * distance);
                if (isReplaceable(extend)) {
                    pos = extend;
                    break;
                }
            }
        }

        for (EnumFacing facing : EnumFacing.values()) {
            Vec3 neighbor = pos.addVector(
                    facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
            if (!isReplaceable(neighbor)) return new Pair<>(neighbor, getOpposite(facing));
        }

        for (EnumFacing facing : EnumFacing.values()) {
            Vec3 neighbor = pos.addVector(
                    facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
            if (isReplaceable(neighbor)) {
                for (EnumFacing dir : EnumFacing.values()) {
                    Vec3 next = neighbor.addVector(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ());
                    if (!isReplaceable(next)) return new Pair<>(next, getOpposite(dir));
                }
            }
        }

        return null;
    }
}
