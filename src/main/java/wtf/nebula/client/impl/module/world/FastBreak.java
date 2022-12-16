package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldSettings;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.event.impl.player.EventUpdate;
import wtf.nebula.client.impl.event.impl.render.EventRender3D;
import wtf.nebula.client.impl.event.impl.world.EventClickBlock;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.player.RotationUtils;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.impl.three.FilledBox;
import wtf.nebula.client.utils.render.renderers.impl.three.OutlinedBox;
import wtf.nebula.client.utils.world.WorldUtils;

import java.awt.*;

public class FastBreak extends ToggleableModule {
    private final Property<Mode> mode = new Property<>(Mode.PACKET, "Mode", "m", "type");
    private final Property<Boolean> auto = new Property<>(false, "Auto Break", "auto");
    private final Property<Boolean> autoSwitch = new Property<>(true, "Auto Switch", "autoswitch", "swap");
    private final Property<Boolean> rotate = new Property<>(false, "Rotate", "rot", "face");
    private final Property<Boolean> render = new Property<>(true, "Render", "draw");

    private int x, y, z;
    private int face = -1;
    private boolean sentBreak = false;
    private boolean broken = false;
    private boolean swapped = false;

    private double progress = 0.0;
    private static int slot = -1;

    public FastBreak() {
        super("Fast Break", new String[]{"fastbreak", "breaker", "fastmine"}, ModuleCategory.WORLD);
        offerProperties(mode, auto, autoSwitch, rotate, render);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        if (swapped && !isNull()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            swapped = false;
        }

        x = -1;
        y = -1;
        z = -1;
        face = -1;

        if (sentBreak && !isNull()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(1, x, y, z, -1));
        }

        sentBreak = false;
        broken = false;
    }

    @EventListener
    public void onUpdate(EventUpdate event) {
        if (sentBreak) {
            if (WorldUtils.isReplaceable(x, y, z)) {
                sentBreak = false;

                if (swapped) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    swapped = false;
                }
            } else {
                if (mc.thePlayer.getDistance(x, y, z) >= mc.playerController.getBlockReachDistance()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(1, x, y, z, -1));
                    sentBreak = false;
                    broken = false;

                    if (swapped) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        swapped = false;
                    }

                    return;
                }

                progress += getStrength(x, y, z, slot);
                if (progress >= 0.94) {

                    if (rotate.getValue()) {
                        float[] rotations = RotationUtils.calcAngles(new Vec3(Vec3.fakePool, x, y, z), face == -1 ? EnumFacing.UP : EnumFacing.faceList[face]);
                        Nebula.getInstance().getRotationManager().setRotations(rotations);
                    }

                    if (autoSwitch.getValue() && slot != -1 && Nebula.getInstance().getInventoryManager().serverSlot != slot) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                    }
                }

                if (progress >= 0.98 && auto.getValue() && !broken) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, face));
                    broken = true;
                }
            }
        }
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onRenderWorld(EventRender3D event) {
        if (sentBreak && render.getValue()) {

            AxisAlignedBB bb = WorldUtils.getBlock(x, y, z).getBaseBoundingBox(); //.getSelectedBoundingBoxFromPool(mc.theWorld, x, y, z);
            if (bb == null) {
                bb = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
            }

            bb = bb.offset(x, y, z);

            Vec3 center = bb.getCenter();
            AxisAlignedBB box = new AxisAlignedBB(center, 0.0);

            double factor = MathHelper.clamp_double(progress, 0.0, 1.0);
            box = box.expand(factor * 0.5, factor * 0.5, factor * 0.5);

            int color = (factor >= 0.95 ? new Color(0, 255, 0, 80) : new Color(255, 0, 0, 80)).getRGB();

            box = box.copy().offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

            RenderEngine.of(Dimension.THREE)
                    .add(new FilledBox(box, color))
                    .add(new OutlinedBox(box, color, 1.5f))
                    .render();
        }
    }

    @EventListener
    public void onClickBlock(EventClickBlock event) {
        if (!mc.playerController.currentGameType.equals(WorldSettings.GameType.SURVIVAL) || mc.thePlayer.isBlocking()) {
            return;
        }

        int posX = event.getX();
        int posY = event.getY();
        int posZ = event.getZ();

        Block blockAt = WorldUtils.getBlock(posX, posY, posZ);
        if (blockAt.equals(Blocks.air) || blockAt.blockHardness == -1) {
            return;
        }

        event.setCancelled(mode.getValue().equals(Mode.PACKET));

        if (posX == x && posY == y && posZ == z && sentBreak) {

            if (progress >= 0.9 && !broken && !auto.getValue()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, face));
                broken = true;
            }
            return;
        }

        if (sentBreak && (posX != x && posY != y && posZ != z)) {
            progress = 0.0;
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(1, x, y, z, -1));
            sentBreak = false;
        }

        x = posX;
        y = posY;
        z = posZ;
        face = event.getFacing();
        sentBreak = true;
        progress = 0.0;

        slot = Nebula.getInstance().getInventoryManager().serverSlot;

        if (autoSwitch.getValue()) {
            int nSlot = AutoTool.getBestToolAt(x, y, z);
            if (slot != nSlot && nSlot != -1) {
                swapped = true;
                slot = nSlot;
                // mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
            }
        }

        if (mode.getValue().equals(Mode.PACKET)) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, face));
            broken = false;
        } else if (mode.getValue().equals(Mode.DAMAGE)) {
            mc.playerController.curBlockDamageMP *= 5.0f;
        } else if (mode.getValue().equals(Mode.INSTANT)) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, face));
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, face));
            mc.theWorld.setBlockToAir(x, y, z);
        }
    }

    @EventListener
    public void onPacket(EventPacket event) {

    }

    public static double getStrength(int x, int y, int z, int slot) {
        Block at = WorldUtils.getBlock(x, y, z);
        ItemStack held = mc.thePlayer.inventory.getStackInSlot(slot);

        float hardness = at.blockHardness;
        if (hardness < 0.0f) {
            return 0.0f;
        }

        double s = getDestroySpeed(x, y, z, slot);
        double f = (held != null && held.func_150998_b(at)) ? 30.0f : 100.0f;
        return s / hardness / f;
    }

    private static double getDestroySpeed(int x, int y, int z, int slot) {

        Block at = WorldUtils.getBlock(x, y, z);
        ItemStack held = mc.thePlayer.inventory.getStackInSlot(slot);

        float breakSpeed = 1.0f;
        if (held != null) {
            breakSpeed *= held.func_150997_a(at);
        }

        if (breakSpeed > 1.0f) {
            int effMod = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, held);
            if (effMod > 0) {
                float mod = effMod * effMod + 1.0f;

                if (!held.func_150998_b(at)) {
                    breakSpeed += mod * 0.08f;
                } else {
                    breakSpeed += mod;
                }
            }
        }

        if (mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            int hasteMod = mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier();
            if (hasteMod > 0) {
                breakSpeed *= 1.0f + (hasteMod + 1.0f) * 0.2f;
            }
        }

        if (mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
            int fatigueMod = mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier();
            if (fatigueMod > 0) {
                breakSpeed *= 1.0f - (fatigueMod + 1.0f) * 0.2f;
            }
        }

        if (mc.thePlayer.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(mc.thePlayer)) {
            breakSpeed /= 5.0f;
        }

//        if (!mc.thePlayer.onGround) {
//            breakSpeed /= 5.0f;
//        }

        return breakSpeed;
    }

    public enum Mode {
        PACKET, INSTANT, DAMAGE
    }
}
