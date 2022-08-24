package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import wtf.nebula.event.ClickBlockEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.util.world.BlockUtil;

public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool", ModuleCategory.WORLD);
    }

    @EventListener
    public void onClickBlock(ClickBlockEvent event) {
        int slot = bestSlotForBlock(event.getX(), event.getY(), event.getZ());
        if (slot != -1) {
            mc.thePlayer.inventory.currentItem = slot;
        }
    }

    public static int bestSlotForBlock(int x, int y, int z) {
        Block at = BlockUtil.getBlockFrom(x, y, z);

        int slot = -1;
        float damage = 0.0f;

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null || !(stack.getItem() instanceof ItemTool)) {
                continue;
            }

            ItemTool tool = (ItemTool) stack.getItem();

//            List<Block> effectiveBlocks = Arrays.asList(tool.blocksEffectiveAgainst);
//            if (!effectiveBlocks.contains(at) && !tool.canHarvestBlock(at)) {
//                continue;
//            }

            float speed = tool.efficiencyOnProperMaterial;

            // factor efficiency level
            int effLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
            if (effLevel > 0) {
                speed += Math.pow(effLevel, 2) + 1.0f;
            }

            if (speed > damage) {
                damage = speed;
                slot = i;
            }
        }

        return slot;
    }
}
