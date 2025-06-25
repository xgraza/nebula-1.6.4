package us.nebula.impl.cheat.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.player.EventAttackBlock;
import us.nebula.util.player.InventoryUtil;

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
