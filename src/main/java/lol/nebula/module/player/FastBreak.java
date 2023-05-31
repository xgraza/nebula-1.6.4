package lol.nebula.module.player;

import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.listener.events.render.world.EventRender3D;
import lol.nebula.listener.events.world.EventClickBlock;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings.GameType;

import java.awt.*;

import static java.lang.Integer.MAX_VALUE;
import static lol.nebula.util.render.ColorUtils.withAlpha;
import static lol.nebula.util.render.RenderUtils.*;
import static lol.nebula.util.world.WorldUtils.getBlock;
import static lol.nebula.util.world.WorldUtils.isReplaceable;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author aesthetical
 * @since 05/27/23
 */
public class FastBreak extends Module {
    private final Setting<Double> percent = new Setting<>(0.94, 0.0, 0.01, 1.0, "Percentage");
    private final Setting<Boolean> render = new Setting<>(true, "Render");

    private int x, y, z, face;
    private double breakProgress;
    private boolean beginBreak, finishBreak, swapped;

    public FastBreak() {
        super("Fast Break", "Breaks a block faster with packets", ModuleCategory.PLAYER);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (beginBreak && mc.thePlayer != null) {

            // reset states
            beginBreak = false;
            finishBreak = false;
            breakProgress = 0.0;

            // send abort break packet
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(1, x, y, z, -1));
        }

        // reset xyz and face
        x = -1;
        y = -1;
        z = -1;
        face = -1;

        // sync to client
        if (swapped) Nebula.getInstance().getInventory().sync();
        swapped = false;
    }

    @Listener
    public void onRender3D(EventRender3D event) {

        // if we shouldnt render, the block is air, or we have not even tried to break the block yet, return
        if (!render.getValue() || isReplaceable(x, y, z) || !beginBreak) return;

        // get the bounding box for this block
        AxisAlignedBB bb = getBlock(x, y, z).getSelectedBoundingBoxFromPool(mc.theWorld, x, y, z);

        // if null, create a default bounding box
        if (bb == null) bb = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);

        // create a bounding box off the center of this bounding box and make our own copy
        bb = new AxisAlignedBB(bb.getCenter(), 0.0).copy();

        // get the break factor
        double factor = MathHelper.clamp_double(breakProgress, 0.0, 1.0);

        bb = bb

                // make the bounding box bigger based on the factor
                .expand(factor * 0.5, factor * 0.5, factor * 0.5)

                // offset by render coordinates
                .offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

        // do render shit idc to write comments for that
        glPushMatrix();
        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        int color = (factor >= 0.95
                ? new Color(0, 255, 0, 80)
                : new Color(255, 0, 0, 80)).getRGB();

        setColor(withAlpha(color, 120));
        filledAabb(bb);
        setColor(color);
        outlinedAabb(bb);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    @Listener
    public void onUpdate(EventUpdate event) {

        // if we have not begun to break, return
        if (!beginBreak) return;

        // if the block is already broken by whatever (meaning the block state is air)
        if (isReplaceable(x, y, z)) {

            // sync with client
            if (swapped) Nebula.getInstance().getInventory().sync();

            // resets
            swapped = false;
            beginBreak = false;
            finishBreak = false;
            breakProgress = 0.0;

            x = -1;
            y = -1;
            z = -1;

            // early return
            return;
        }

        double reachDistance = mc.playerController.getBlockReachDistance();

        // if the player distance to the block is greater than the reach distance
        if (mc.thePlayer.getDistanceSq(x, y, z) > reachDistance * reachDistance) {

            // reset states
            beginBreak = false;
            finishBreak = false;
            breakProgress = 0.0;

            // send abort break packet
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(1, x, y, z, -1));

            x = -1;
            y = -1;
            z = -1;

            // early return
            return;
        }

        // the best tool slot for this block
        int slot = AutoTool.getBestSlotFor(getBlock(x, y, z));
        if (slot == -1) slot = Nebula.getInstance().getInventory().getServerSlot();

        // add progress to the block being broken
        breakProgress += getStrength(x, y, z, slot);

        // if we're above the threshold of break percentage (how far the block has been broken)
        if (breakProgress >= percent.getValue()) {

            // swap to best tool slot
            if (slot != -1 && Nebula.getInstance().getInventory().getServerSlot() != slot) {
                swapped = true;
                Nebula.getInstance().getInventory().setSlot(slot);
            }

            if (!finishBreak) {
                finishBreak = true;

                // send the finish break packet
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, face));
            }
        }
    }

    @Listener(eventPriority = MAX_VALUE)
    public void onClickBlock(EventClickBlock event) {

        // if we're not in survival - we can instant break things in creative and cant break things in adventure mode
        // or if we're blocking our sword or are using an item, return
        if (!mc.playerController.currentGameType.equals(GameType.SURVIVAL)
                || mc.thePlayer.isBlocking() || mc.thePlayer.isUsingItem()) return;

        // get the block at these coordinates from the event
        Block blockAt = getBlock(event.getX(), event.getY(), event.getZ());

        // if the block cannot be broken or is air, return
        if (blockAt.equals(Blocks.air) || blockAt.blockHardness < 0.0f) return;

        // do not let block get broken by the client
        event.cancel();

        // do not try to do anything while already breaking
        if (x == event.getX() && y == event.getY() && z == event.getZ()) return;

        // if we have begun to break another block
        if (beginBreak) {

            // reset states
            beginBreak = false;
            finishBreak = false;
            breakProgress = 0.0;

            // send abort break packet
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(1, x, y, z, -1));
        }

        // set xyz and face
        x = event.getX();
        y = event.getY();
        z = event.getZ();
        face = event.getFacing().getOrder_a();

        breakProgress = 0.0;
        beginBreak = true;

        // begin to break
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, face));
    }

    public static double getStrength(int x, int y, int z, int slot) {
        Block blockAt = getBlock(x, y, z);
        ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(slot);

        if (blockAt.blockHardness < 0.0f) return 0.0f;

        double s = getDestroySpeed(x, y, z, slot);
        double f = (itemStack != null && itemStack.func_150998_b(blockAt)) ? 30.0f : 100.0f;
        return s / blockAt.blockHardness / f;
    }

    private static double getDestroySpeed(int x, int y, int z, int slot) {

        Block blockAt = getBlock(x, y, z);
        ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(slot);

        float breakSpeed = 1.0f;
        if (itemStack != null) breakSpeed *= itemStack.func_150997_a(blockAt);

        if (breakSpeed > 1.0f) {
            int effMod = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
            if (effMod > 0) {
                float mod = effMod * effMod + 1.0f;
                breakSpeed += (itemStack.func_150998_b(blockAt) ? mod : mod * 0.08f);
            }
        }

        if (mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            int hasteMod = mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier();
            if (hasteMod > 0) breakSpeed *= 1.0f + (hasteMod + 1.0f) * 0.2f;
        }

        if (mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
            int fatigueMod = mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier();
            if (fatigueMod > 0) breakSpeed *= 1.0f - (fatigueMod + 1.0f) * 0.2f;
        }

        if (mc.thePlayer.isInsideOfMaterial(Material.water)
                && !EnchantmentHelper.getAquaAffinityModifier(mc.thePlayer)) breakSpeed /= 5.0f;

        if (!mc.thePlayer.onGround) breakSpeed /= 5.0f; // TODO: ground spoof option?

        return breakSpeed;
    }
}
