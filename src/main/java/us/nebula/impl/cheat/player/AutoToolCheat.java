package us.nebula.impl.cheat.player;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.player.EventAttackBlock;
import us.nebula.util.player.ItemUtil;

/**
 * @author xgraza
 * @since 03/26/25
 */
@CheatManifest(name = "AutoTool",
        description = "Automatically switches to the best tool available",
        category = CheatCategory.PLAYER)
public final class AutoToolCheat extends Cheat
{
    @Subscribe
    private final EventListener<EventAttackBlock> attackBlockEventListener = event ->
    {
        if (MC.playerController.isInCreativeMode())
        {
            return;
        }
        final Block block = MC.theWorld.getBlock(event.getX(), event.getY(), event.getZ());
        if (block == Blocks.air || block.blockHardness == -1.0f)
        {
            return;
        }
        final int slot = getSlot(block);
        if (slot == -1)
        {
            return;
        }
        MC.thePlayer.inventory.currentItem = slot;
    };

    private int getSlot(final Block attackedBlock)
    {
        float bestScore = 1.0f;
        int slot = -1;
        for (int i = 0; i < 9; ++i)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null)
            {
                continue;
            }
            final float score = getToolScore(itemStack, attackedBlock);
            if (score > bestScore)
            {
                bestScore = score;
                slot = i;
            }
        }
        return slot;
    }

    private float getToolScore(final ItemStack itemStack, final Block attackedBlock)
    {
        float damage = itemStack.getStrVsBlock(attackedBlock);
        if (damage <= 1.0f)
        {
            return 0.0f;
        }
        damage += ItemUtil.getEnchantLevel(Enchantment.unbreaking, itemStack) * 1.5f;
        damage += ItemUtil.getEnchantLevel(Enchantment.efficiency, itemStack) * 1.2f;
        return damage;
    }
}
