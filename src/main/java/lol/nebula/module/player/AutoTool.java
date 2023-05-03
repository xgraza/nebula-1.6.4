package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.world.EventClickBlock;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

/**
 * @author aesthetical
 * @since 05/03/23
 */
public class AutoTool extends Module {
    public AutoTool() {
        super("Auto Tool", "Automatically swaps to the best tool", ModuleCategory.PLAYER);
    }

    @Listener
    public void onClickBlock(EventClickBlock event) {
        Block block = mc.theWorld.getBlock(event.getX(), event.getY(), event.getZ());

        int slot = getBestSlotFor(block);
        if (slot == -1) return;

        mc.thePlayer.inventory.currentItem = slot;
    }

    /**
     * Gets the best tool for a block
     * @param block the block
     * @return the best slot or -1
     */
    private int getBestSlotFor(Block block) {
        int slot = -1;
        float damage = 0.0f;

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null) continue;

            float dmg = getToolDamage(stack, block);
            if (dmg <= 1.0f) continue;

            if (dmg > damage) {
                damage = dmg;
                slot = i;
            }
        }

        return slot;
    }

    /**
     * Gets the tool damage
     * @param stack the stack
     * @param block the block
     * @return the strVsBlock + enchantment or * 0.6 if gold material
     */
    private float getToolDamage(ItemStack stack, Block block) {

        float score = stack.func_150997_a(block);

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 1.3f;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) * 0.5f;

        if (stack.getItem() instanceof ItemTool) {
            ItemTool itemTool = (ItemTool) stack.getItem();
            if (itemTool.func_150913_i().equals(ToolMaterial.GOLD)) score *= 0.6f;
        }

        return Math.max(score, 0.0f);
    }
}
