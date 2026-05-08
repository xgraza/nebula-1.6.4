package us.nebula.client.cheat.impl.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.BlockFire;
import net.minecraft.util.AxisAlignedBB;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.world.EventModifyBoundBox;

/**
 * @author xgraza
 * @since 03/07/25
 */
@CheatManifest(name = "AntiBlock",
        description = "Prevents you from walking into blocks",
        category = CheatCategory.PLAYER)
public final class AntiBlockCheat extends Cheat
{
    private static final AxisAlignedBB FULL_BLOCK_AABB = new AxisAlignedBB(
            0, 0, 0, 1, 1, 1);

    private final Setting<Boolean> exemptSneaking = new Setting<>(
            "Exempt Sneak", false);
    private final Setting<Boolean> cactusSetting = new Setting<>(
            "Cactus", false);
    private final Setting<Boolean> endPortalSetting = new Setting<>(
            "End Portals", false);
    private final Setting<Boolean> fireSetting = new Setting<>(
            "Fire", false);

    @Subscribe
    private final EventListener<EventModifyBoundBox> modifyBoundBoxEventListener = event ->
    {
        if (MC.thePlayer == null || MC.theWorld == null)
        {
            return;
        }

        if (!MC.thePlayer.equals(event.getEntity()) || !MC.theWorld.equals(event.getWorld()))
        {
            return;
        }
        if (exemptSneaking.getValue() && MC.gameSettings.keyBindSneak.pressed)
        {
            return;
        }
        final Block block = MC.theWorld.getBlock(event.getX(), event.getY(), event.getZ());
        if (isWhitelisted(block))
        {
            event.setAabb(FULL_BLOCK_AABB.copy().offset(event.getX(), event.getY(), event.getZ()));
        }
    };

    public boolean isWhitelisted(final Block block)
    {
        return (block instanceof BlockCactus && cactusSetting.getValue())
                || (block instanceof BlockEndPortal && endPortalSetting.getValue())
                || (block instanceof BlockFire && fireSetting.getValue());
    }
}
