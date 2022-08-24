package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldSettings;
import wtf.nebula.event.ClickBlockEvent;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
import wtf.nebula.event.RenderWorldEvent;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.Timer;
import wtf.nebula.util.render.ColorUtil;
import wtf.nebula.util.render.RenderUtil;
import wtf.nebula.util.world.BlockUtil;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class PacketMine extends Module {
    public PacketMine() {
        super("PacketMine", ModuleCategory.WORLD);
    }

    public final Value<Boolean> autoSwitch = new Value<>("AutoSwitch", true);
    public final Value<Boolean> autoBreak = new Value<>("AutoBreak", true);
    public final Value<Boolean> groundCheck = new Value<>("GroundCheck", true);
    public final Value<Boolean> render = new Value<>("Render", true);

    private int posX = -1;
    private int posY = -1;
    private int posZ = -1;

    private int facing;

    private boolean sent = false;
    private boolean swapped = false;

    private double speed = 0.0;

    private final Timer timer = new Timer();

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        if (sent && autoBreak.getValue()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(1, posX, posY, posZ, facing));
        }

        syncItem();

        facing = -1;

        sent = false;
        swapped = false;

        posX = -1;
        posY = -1;
        posZ = -1;
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {

        if (sent && render.getValue()) {
            boolean passed = timer.passedTime((long) (speed * 1000.0), false);

            int color = (passed ? Color.green : Color.red).getRGB();
            color = ColorUtil.addAlpha(color, 80);

            glPushMatrix();

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);

            ColorUtil.setColor(color);

            RenderUtil.drawFilledBoundingBox(new AxisAlignedBB(
                    posX, posY, posZ, posX + 1.0, posY + 1.0, posZ + 1.0
            ).offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ));

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);

            glPopMatrix();
        }
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (sent && BlockUtil.isReplaceable(new Vec3(Vec3.fakePool, posX, posY, posZ))) {
            posX = -1;
            posY = -1;
            posZ = -1;

            facing = -1;
            sent = false;

            syncItem();
        }
    }

    @EventListener
    public void onClickBlock(ClickBlockEvent event) {

        // i like the block hit the ground while walking so this is a fix to something annoying
        if (mc.thePlayer.isBlocking()) {
            return;
        }

        if (mc.playerController.currentGameType.equals(WorldSettings.GameType.CREATIVE)) {
            return;
        }

        int x = event.getX();
        int y = event.getY();
        int z = event.getZ();

        Vec3 v = new Vec3(Vec3.fakePool, x, y, z);

        // block is unbreakable (hardness is -1) examples are bedrock, command blocks, etc
        Block block = BlockUtil.getBlockFromVec(v);
        if (block != null && block.getBlockHardness(mc.theWorld, x, y, z) == -1) {
            return;
        }

        if (posX == x && posY == y && posZ == z && sent) {

            // send block break packet
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, facing));
            return;
        }

        if ((posX != x && posY != y && posZ != z && sent) || mc.playerController.isHittingBlock) {
            // line 179 in PlayerControllerMP
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(1, x, y, z, facing));
            sent = false;
        }

        // reset block breaking
        mc.playerController.resetBlockRemoving();

        int slot = mc.thePlayer.inventory.currentItem;

        // if we should auto switch
        if (autoSwitch.getValue()) {
            int s = AutoTool.bestSlotForBlock((int) v.xCoord, (int) v.yCoord, (int) v.zCoord);
            if (s != -1) {
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(s));
                swapped = true;

                slot = s;
            }
        }

        speed = getDestroySpeed(v, slot);

        if (!sent) {
            sent = true;

            posX = x;
            posY = y;
            posZ = z;

            facing = event.getFacing();

            // start breaking
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, facing));

            if (autoBreak.getValue()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(2, posX, posY, posZ, facing));
            }

            // reset our autoBreak timer
            timer.resetTime();
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {

        // prevent us from swapping to anything else
        if (event.getPacket() instanceof C09PacketHeldItemChange && event.getEra().equals(Era.PRE) && swapped) {
            event.setCancelled(true);
        }

        if (event.getPacket() instanceof C03PacketPlayer && !groundCheck.getValue() && sent) {
            ((C03PacketPlayer) event.getPacket()).onGround = true;
        }
    }

    private void syncItem() {
        if (swapped) {
            swapped = false;
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
    }

    public double getDestroySpeed(Vec3 pos, int slot) {
        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(slot);

        Block at = BlockUtil.getBlockFromVec(pos);
        float multiplier = 1.0f;
        if (stack != null) {
            // TODO: find method name
            //multiplier = stack.getStrVsBlock(at);
        }

        if (stack != null && EnchantmentHelper.getEnchantments(stack).containsKey(Enchantment.efficiency.effectId)) {
            int eff = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
            multiplier += Math.pow(eff, 2) + 1.0f;
        }

        if (mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            PotionEffect effect = mc.thePlayer.getActivePotionEffect(Potion.digSpeed);
            multiplier *= 1 + (0.2 * effect.getAmplifier());
        }

        if (mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
            int level = mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier();
            switch (level) {
                case 1:
                    multiplier *= 0.3;
                    break;

                case 2:
                    multiplier *= 0.09;
                    break;

                case 3:
                    multiplier *= 0.0027;
                    break;

                default:
                    multiplier *= 0.00081;
                    break;
            }
        }

        if (
                (mc.thePlayer.isInsideOfMaterial(Material.water)
                        && !EnchantmentHelper.getAquaAffinityModifier(mc.thePlayer))
                        || (!mc.thePlayer.onGround && groundCheck.getValue())) {
            multiplier /= 5.0;
        }

        float hardness = 0.5f;
        if (at != null) {
            hardness = at.getBlockHardness(null, 0, 0, 0);
        }

        float dmg = multiplier / hardness;

        if (stack != null){ //&& stack.canHarvestBlock(at)) {
            dmg /= 30.0f;
        }

        else {
            dmg /= 100.0f;
        }

        if (dmg > 1.0f) {
            return 0.0;
        }

        // TODO: tps sync?
        return Math.ceil(1 / dmg) / 20.0;
    }
}
