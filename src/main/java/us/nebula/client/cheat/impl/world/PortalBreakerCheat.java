package us.nebula.client.cheat.impl.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import us.nebula.client.Nebula;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.interaction.InteractionManager;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.listener.event.player.EventAttackBlock;
import us.nebula.client.listener.event.world.EventModifyBoundBox;
import us.nebula.client.listener.event.world.EventModifySelectedBoundBox;
import us.nebula.client.util.player.ChatUtil;
import us.nebula.client.util.player.InventoryUtil;
import us.nebula.client.util.world.BlockUtil;

/**
 * @author xgraza
 * @since 05/24/26
 */
@CheatManifest(name = "PortalBreaker",
        description = "Breaks placed end portal blocks when you click them",
        category = CheatCategory.WORLD)
public final class PortalBreakerCheat extends Cheat
{
    private int slot = -1;
    private BlockPos pos;
    private boolean trying;

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (MC.thePlayer != null && slot != -1)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
        slot = -1;
        pos = null;
        trying = false;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (slot == -1 || pos == null)
        {
            return;
        }

        BlockPos placePos = null;
        EnumFacing placeFace = null;

        for (final EnumFacing facing : EnumFacing.values())
        {
            if (facing == EnumFacing.UP || facing == EnumFacing.DOWN)
            {
                continue;
            }
            final BlockPos neighborPos = pos.offset(facing);
            if (!BlockUtil.isReplaceable(neighborPos))
            {
                placePos = neighborPos;
                placeFace = BlockUtil.getOpposite(facing);
                break;
            }
        }
        if (placePos == null || placeFace == null)
        {
            pos = null;
            slot = -1;
            return;
        }

        if (trying)
        {
            return;
        }
        trying = true;

        ChatUtil.sendNebula("%s, (%s)", placePos, placeFace);

        ChatUtil.sendNebula("Swapping to %s", slot);
        Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        ChatUtil.sendNebula("Placing water bucket");

        InteractionManager.INSTANCE.rightClickBlock(placePos, placeFace); // click w/ water bucket
        ChatUtil.sendNebula("Collecting water bucket");
        //InteractionManager.INSTANCE.rightClickBlock(placePos, placeFace); // collect water
        ChatUtil.sendNebula("Synced");
        Nebula.INSTANCE.getInventoryManager().syncSlot();

        slot = -1;
        pos = null;
        trying = false;
    };

    @Subscribe
    private final EventListener<EventModifySelectedBoundBox> modifySelectedBoundBoxEventListener = event ->
    {
        final Block block = event.getWorld().getBlock(event.getX(), event.getY(), event.getZ());
        if (!(block instanceof BlockEndPortal))
        {
            return;
        }
        event.setAabb(new AxisAlignedBB(new BlockPos(event.getX(), event.getY(), event.getZ())));
    };

    @Subscribe
    private final EventListener<EventAttackBlock> attackBlockEventListener = event ->
    {
        final BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        if (!(MC.theWorld.getBlock(pos) instanceof BlockEndPortal))
        {
            return;
        }

        slot = InventoryUtil.getSlot(0, 9,
                (stack) -> stack.getItem() == Items.water_bucket);
        if (slot == -1)
        {
            notifyInfo("You need a water bucket in your hotbar", 7500L);
            return;
        }
        this.pos = pos;
        event.cancel();
    };
}
