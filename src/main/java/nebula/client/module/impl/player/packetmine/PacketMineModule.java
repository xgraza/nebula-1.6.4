package nebula.client.module.impl.player.packetmine;

import nebula.client.Nebula;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.interact.EventClickBlock;
import nebula.client.listener.event.player.EventUpdate;
import nebula.client.listener.event.render.EventRender3D;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.module.impl.player.autotool.AutoToolModule;
import nebula.client.util.render.ColorUtils;
import nebula.client.util.render.RenderUtils;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Gavin
 * @since 08/18/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "PacketMine",
  description = "Mines things with packets")
public class PacketMineModule extends Module {

  @SettingMeta("Instant")
  private final Setting<Boolean> instant = new Setting<>(
    true);
  @SettingMeta("Rotate")
  private final Setting<Boolean> rotate = new Setting<>(
    true);

  private final Queue<MinePosition> mineQueue = new ConcurrentLinkedQueue<>();
  private MinePosition current;

  private double mineProgress;
  private boolean mining, broke;

  @Override
  public void disable() {
    super.disable();

    if (mining && current != null && mc.thePlayer != null) {
      // ABORT_BREAK
      mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
        1, current.x(), current.y(), current.z(), -1));
    }

    mineQueue.clear();
    current = null;
    mining = false;
    broke = false;
    mineProgress = 0.0;

    Nebula.INSTANCE.inventory.sync();
  }

  @Subscribe
  private final Listener<EventRender3D> eventRender3D = event -> {
    // if we shouldnt render, the block is air, or we have not even tried to break the block yet, return
    if (current == null || !mining) return;

    int x = current.x();
    int y = current.y();
    int z = current.z();

    // get the bounding box for this block
    AxisAlignedBB bb = mc.theWorld.getBlock(x, y, z).getSelectedBoundingBoxFromPool(mc.theWorld, x, y, z);

    // if null, create a default bounding box
    if (bb == null) bb = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);

    // create a bounding box off the center of this bounding box and make our own copy
    bb = new AxisAlignedBB(bb.getCenter(), 0.0).copy();

    // get the break factor
    double factor = MathHelper.clamp_double(mineProgress, 0.0, 1.0);

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

    RenderUtils.color(ColorUtils.alpha(color, 120));
    RenderUtils.filledAabb(bb);
    RenderUtils.color(color);
    RenderUtils.outlinedAabb(bb);

    glEnable(GL_DEPTH_TEST);
    glEnable(GL_TEXTURE_2D);
    glDisable(GL_BLEND);
    glPopMatrix();
  };

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (current == null) {
      current = mineQueue.poll();
      return;
    }

    int slot = AutoToolModule.bestSlotFor(
      current.x(), current.y(), current.z());
    if (slot == -1) slot = mc.thePlayer.inventory.currentItem;

    if (!mining) {
      mining = true;
      mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
        0, current.x(), current.y(), current.z(), current.face()));
    }

    double distance = mc.thePlayer.getDistanceSq(current.x() + 0.5,
      current.y() + 1.0, current.z() + 0.5);
    if (distance >= 4.5 * 4.5) {
      mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
        1, current.x(), current.y(), current.z(), -1));
      current = null;

      mining = false;
      broke = false;
      mineProgress = 0.0;

      Nebula.INSTANCE.inventory.sync();

      return;
    }

    if (mc.theWorld.getBlock(current.x(), current.y(), current.z()).getMaterial().isReplaceable()) {
      current = null;

      mining = false;
      broke = false;
      mineProgress = 0.0;

      Nebula.INSTANCE.inventory.sync();
      return;
    }

    if (instant.value() && canInstantBreak(current.x(), current.y(), current.z(), slot)) {
      mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
      mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
        2, current.x(), current.y(), current.z(), current.face()));

      mc.theWorld.setBlockToAir(current.x(), current.y(), current.z());

      mining = false;
      broke = false;
      mineProgress = 0.0;

      Nebula.INSTANCE.inventory.sync();
    } else {
      mineProgress += getStrength(current.x(),
        current.y(), current.z(), slot);
      if (mineProgress >= 0.98 && !broke) {
        broke = true;
        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
          2, current.x(), current.y(), current.z(), current.face()));
      }
    }
  };

  @Subscribe
  private final Listener<EventClickBlock> clickBlock = event -> {
    Block block = mc.theWorld.getBlock(
      event.x(), event.y(), event.z());
    if (block.blockHardness == -1.0f) return;

    event.setCanceled(true);

    MinePosition minePosition = new MinePosition(
      event.x(), event.y(), event.z(), event.face());
    if (!mineQueue.contains(minePosition)) {
      mineQueue.add(minePosition);
    }
  };

  private boolean canInstantBreak(int x, int y, int z, int slot) {
    Block at = mc.theWorld.getBlock(x, y, z);
    ItemStack held = mc.thePlayer.inventory.getStackInSlot(slot);

    float hardness = at.blockHardness;
    if (hardness < 0.0f) return false;

    double progress = getStrength(x, y, z, slot);
    return progress >= 1.0;
  }

  public static double getStrength(int x, int y, int z, int slot) {
    Block at = mc.theWorld.getBlock(x, y, z);
    ItemStack held = mc.thePlayer.inventory.getStackInSlot(slot);

    float hardness = at.blockHardness;
    if (hardness < 0.0f) return 0.0f;

    double s = getDestroySpeed(x, y, z, slot);
    double f = (held != null && held.func_150998_b(at)) ? 30.0f : 100.0f;
    return s / hardness / f;
  }

  private static double getDestroySpeed(int x, int y, int z, int slot) {

    Block at = mc.theWorld.getBlock(x, y, z);
    ItemStack held = mc.thePlayer.inventory.getStackInSlot(slot);

    float breakSpeed = 1.0f;
    if (held != null) {
      breakSpeed *= held.func_150997_a(at);
    }

    if (breakSpeed > 1.0f) {
      int effMod = EnchantmentHelper.getEnchantmentLevel(
        Enchantment.efficiency.effectId, held);
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

    if (mc.thePlayer.isInsideOfMaterial(Material.water)
      && !EnchantmentHelper.getAquaAffinityModifier(mc.thePlayer)) {
      breakSpeed /= 5.0f;
    }

    if (!mc.thePlayer.onGround) {
      breakSpeed /= 5.0f;
    }

    return breakSpeed;
  }
}
