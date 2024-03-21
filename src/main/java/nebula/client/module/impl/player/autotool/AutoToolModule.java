package nebula.client.module.impl.player.autotool;

import com.google.common.collect.Lists;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.interact.EventClickBlock;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.player.ItemUtils;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author Gavin
 * @since 08/18/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "AutoTool",
  description = "Automatically switches to the best tool")
public class AutoToolModule extends Module {

  private static final List<Enchantment> toolEnchantments = Lists.newArrayList(
    Enchantment.unbreaking, Enchantment.looting,
    Enchantment.efficiency, Enchantment.silkTouch);

  @SettingMeta("Silk Touch")
  private static final Setting<Boolean> preferSilkTouch = new Setting<>(
    false);

  @Subscribe
  private final Listener<EventClickBlock> clickBlock = event -> {
    Block block = mc.theWorld.getBlock(
      event.x(), event.y(), event.z());
    if (block.blockHardness == -1.0f) return;

    int slot = bestSlotFor(event.x(), event.y(), event.z());
    if (slot != -1) mc.thePlayer.inventory.currentItem = slot;
  };

  public static int bestSlotFor(int x, int y, int z) {

    Block block = mc.theWorld.getBlock(x, y, z);
    if (block.blockHardness == -1.0f) return -1;

    int slot = -1;
    float damage = 1.0f;

    for (int i = 0; i < 9; ++i) {
      ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
      if (itemStack == null) continue;

      if (itemStack.func_150998_b(block)) {
        float strVsBlock = itemStack.func_150997_a(block);
        if (strVsBlock > 1.0f) {
          for (Enchantment enchantment : toolEnchantments) {
            if (enchantment == Enchantment.silkTouch
              && !preferSilkTouch.value()) continue;

            strVsBlock += ItemUtils.enchantment(
              enchantment.effectId, itemStack) * 1.2f;
          }

          if (strVsBlock > damage) {
            damage = strVsBlock;
            slot = i;
          }
        }
      }
    }

    return slot;
  }
}
