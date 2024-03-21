package nebula.client.module.impl.player.scaffold;

import nebula.client.Nebula;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.net.EventPacket;
import nebula.client.listener.event.player.EventUpdate;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.rotate.Rotation;
import nebula.client.util.chat.Printer;
import nebula.client.util.math.AngleUtils;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import static nebula.client.util.player.ItemUtils.infinite;
import static org.lwjgl.input.Keyboard.KEY_N;

/**
 * @author Gavin
 * @since 08/16/23
 */
@ModuleMeta(name = "Scaffold",
  description = "Scaffolds and shit",
  defaultMacro = KEY_N)
public class ScaffoldModule extends Module {

  /**
   * The rotation priority for the Scaffold module
   */
  private static final int ROTATION_PRIORITY = 10;

  @SettingMeta("Swing")
  private final Setting<Boolean> swing = new Setting<>(
    true);
  @SettingMeta("Rotate")
  private final Setting<Boolean> rotate = new Setting<>(
    true);
  @SettingMeta("Downwards")
  private final Setting<Boolean> downwards = new Setting<>(
    false);
  @SettingMeta("Tower")
  private final Setting<Boolean> tower = new Setting<>(
          true);

  private int towerTicks;

  @Override
  public void disable() {
    super.disable();

    Nebula.INSTANCE.inventory.sync();
    Nebula.INSTANCE.rotation.reset(ROTATION_PRIORITY);

    towerTicks = 0;
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    ScaffoldPlacement placement = next();
    if (placement == null) return;

    int slot = slot();
    if (slot == -1) return;

    if (rotate.value()) {
      Nebula.INSTANCE.rotation.submit(Rotation.from(
        AngleUtils.block(placement.pos(), placement.facing()),
        ROTATION_PRIORITY, 5));
    }

    if (Nebula.INSTANCE.inventory.slot() != slot) {
      mc.thePlayer.sendQueue.addToSendQueue(
        new C09PacketHeldItemChange(slot));
    }

    boolean result = mc.playerController.onPlayerRightClick(mc.thePlayer,
      mc.theWorld,
      mc.thePlayer.inventory.getStackInSlot(slot),
      (int) placement.pos().xCoord,
      (int) placement.pos().yCoord,
      (int) placement.pos().zCoord,
      placement.facing().order_a,
      placement.pos().addVector(0.5, 0.5, 0.5));

    Nebula.INSTANCE.inventory.sync();

    if (!result) return;

    if (tower.value() && mc.gameSettings.keyBindJump.pressed) {

      ++towerTicks;

      if (mc.thePlayer.onGround || mc.thePlayer.motionY == 0.16477328182606651) {
        mc.thePlayer.motionY = 0.42f;
      }

    } else {
      towerTicks = 0;
    }

    if (swing.value()) {
      mc.thePlayer.swingItem();
    } else {
      mc.thePlayer.swingSilent();
    }

  };

  private int slot() {
    ItemStack itemStack = mc.thePlayer.getHeldItem();
    if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
      return mc.thePlayer.inventory.currentItem;
    }

    int slot = -1;
    int stackSize = 0;

    for (int i = 0; i < 9; ++i) {
      ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
      if (stack != null && stack.getItem() instanceof ItemBlock) {
        if (infinite(stack)) return i;

        if (stack.stackSize > stackSize && stack.stackSize != 0) {
          slot = i;
          stackSize = stack.stackSize;
        }
      }
    }

    return slot;
  }

  private ScaffoldPlacement next() {
    Vec3 pos = Vec3.createVectorHelper(
      Math.floor(mc.thePlayer.posX),
      mc.thePlayer.boundingBox.minY - (downwards.value() ? 2 : 1),
      Math.floor(mc.thePlayer.posZ));

    for (EnumFacing facing : EnumFacing.values()) {
      Vec3 n = pos.addVector(facing.getFrontOffsetX(),
        facing.getFrontOffsetY(),
        facing.getFrontOffsetZ());
      if (!replaceable(n)) {
        return new ScaffoldPlacement(n,
          EnumFacing.values()[facing.order_b]);
      }
    }

    for (EnumFacing facing : EnumFacing.values()) {
      Vec3 n = pos.addVector(facing.getFrontOffsetX(),
        facing.getFrontOffsetY(),
        facing.getFrontOffsetZ());
      if (replaceable(n)) {

        for (EnumFacing f : EnumFacing.values()) {
          Vec3 nn = n.addVector(f.getFrontOffsetX(),
            f.getFrontOffsetY(),
            f.getFrontOffsetZ());
          if (!replaceable(nn)) {
            return new ScaffoldPlacement(nn, EnumFacing.values()[f.order_b]);
          }
        }
      }
    }

    return null;
  }

  private boolean replaceable(Vec3 pos) {
    return mc.theWorld.getBlock((int) pos.xCoord,
      (int) pos.yCoord,
      (int) pos.zCoord)
      .getMaterial()
      .isReplaceable();
  }
}
