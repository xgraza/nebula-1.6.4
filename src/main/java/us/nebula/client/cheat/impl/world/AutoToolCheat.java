package us.nebula.client.cheat.impl.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.player.EventAttackBlock;
import us.nebula.client.util.player.InventoryUtil;

/**
 * @author xgraza
 * @since 03/26/25
 */
@CheatManifest(name = "AutoTool",
        description = "Automatically switches to the best tool available",
        category = CheatCategory.WORLD)
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
        final int slot = InventoryUtil.getBestToolSlotFor(block);
        if (slot == -1)
        {
            return;
        }
        MC.thePlayer.inventory.currentItem = slot;
    };
}
