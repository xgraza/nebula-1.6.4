package ez.nebula.client.impl.module.world;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.world.EventPlace;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.ItemUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.minecraft.world.BlockInfo;
import ez.nebula.client.util.minecraft.world.BlockUtil;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 6/25/26
 */
@ModuleManifest(name = "AutoWither",
        description = "Automatically finishes placing a wither spawn after the first base soul sand block is placed",
        category = ModuleCategory.WORLD)
public final class AutoWitherModule extends Module
{
    private static final int WITHER_HEAD_DAMAGE = 1;

    private int x, y, z, soulSandSlot;

    @Override
    public void onEnable()
    {
        super.onEnable();
        invalidate();
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (MC.thePlayer != null)
        {
            Nebula.INVENTORY.sync();
        }
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if ((x == -1 && y == -1 && z == -1) || soulSandSlot == InventoryUtil.INVALID_SLOT)
        {
            return;
        }
        final int witherHeadSlot = getWitherSkullSlot();
        if (witherHeadSlot == InventoryUtil.INVALID_SLOT)
        {
            return;
        }
        final List<BlockPos> soulSandPositions = getSoulSandPositions();
        if (soulSandPositions == null)
        {
            // ChatUtil.sendNebula("soul sand failure");
            Nebula.INVENTORY.sync();
            invalidate();
            return; // null result = failure
        }
        if (!soulSandPositions.isEmpty())
        {
            for (final BlockPos pos : soulSandPositions)
            {
                final BlockInfo info = BlockUtil.getPlacement(pos);
                if (info == null)
                {
                    continue;
                }
                Nebula.INVENTORY.spoof(soulSandSlot);
                Nebula.INTERACTIONS.rightClickBlock(info.getPos(), info.getFacing(), true);
                Nebula.INVENTORY.sync();
                return;
            }
        }

        for (final BlockPos soulSandPos : soulSandPositions)
        {
            if (!(MC.theWorld.getBlock(soulSandPos) instanceof BlockSoulSand))
            {
                // ChatUtil.sendNebula("no longer soul sand");
                Nebula.INVENTORY.sync();
                invalidate();
                return;
            }
        }

        // ChatUtil.sendNebula("doing wither heads");

        final List<BlockPos> tPosList = getTTop(new BlockPos(x, y + 1, z));
        // ChatUtil.sendNebula("List: %s", tPosList == null ? "null" : tPosList.size());
        if (tPosList == null || tPosList.isEmpty())
        {
            // ChatUtil.sendNebula("Finished placing");
            Nebula.INVENTORY.sync();
            invalidate();
            return;
        }

        final BlockPos pos = tPosList.get(0);
        final BlockInfo info = BlockUtil.getPlacement(pos.up());
        if (info == null)
        {
            return;
        }
        Nebula.INVENTORY.spoof(witherHeadSlot);
        Nebula.INTERACTIONS.rightClickBlock(info.getPos(), info.getFacing(), true);
        Nebula.INVENTORY.sync();
        // we finished placing
        if (tPosList.size() == 1)
        {
            //ChatUtil.sendNebula("Finished placing");
            Nebula.INVENTORY.sync();
            invalidate();
        }
    };

    @Subscribe
    private final EventListener<EventPlace> placeEventListener = event ->
    {
        // WRONG!
        if (event.getSide() == -1 || event.getSide() == 255)
        {
            return;
        }

        if (x != -1 && y != -1 && z != -1)
        {
            // do not override if this module is the one placing the new soulsand blocks
            final double distance = MC.thePlayer.getDistance(x + 0.5, y + 1.0, z + 0.5);
            if (distance <= MC.playerController.getBlockReachDistance())
            {
                return;
            }
        }

        final ItemStack itemStack = event.getItemStack();
        if (itemStack != null
                && itemStack.getItem() instanceof ItemBlock
                && ((ItemBlock) itemStack.getItem()).getBlock() instanceof BlockSoulSand)
        {
            // we need at least 5 blocks of soul sand (pre place) to make a wither
            if (!ItemUtil.isInfinite(itemStack) && itemStack.stackSize < 5)
            {
                return;
            }
            soulSandSlot = Nebula.INVENTORY.slot();
            // get the correct pos via the place face
            final EnumFacing face = EnumFacing.faceList[event.getSide()];
            x = event.getX() + face.getFaceX();
            y = event.getY() + face.getFaceY();
            z = event.getZ() + face.getFaceZ();
            return;
        }
        invalidate();
    };

    private void invalidate()
    {
        soulSandSlot = InventoryUtil.INVALID_SLOT;
        x = y = z = -1;
    }

    private List<BlockPos> getSoulSandPositions()
    {
        if (x == -1 && y == -1 && z == -1)
        {
            return null;
        }
        final BlockPos[] adjacent = BlockUtil.getAdjacent(PlayerUtil.getFacing());
        if (adjacent == null)
        {
            return null;
        }
        final List<BlockPos> posList = new LinkedList<>();
        BlockPos pos = new BlockPos(x, y, z);

        if (BlockUtil.isReplaceable(pos))
        {
            posList.add(pos);
        }
        pos = pos.up(); // start of the T
        if (BlockUtil.isReplaceable(pos))
        {
            // ensure space to place wither head
            if (BlockUtil.isNotAir(pos.up()))
            {
                return null;
            }
            posList.add(pos);
        }

        final BlockPos tOffsetPos1 = pos.add(adjacent[0]);
        if (BlockUtil.isReplaceable(tOffsetPos1))
        {
            // ensure space to place wither head
            if (BlockUtil.isNotAir(tOffsetPos1.up()))
            {
                return null;
            }
            posList.add(tOffsetPos1);
        }

        final BlockPos tOffsetPos2 = pos.add(adjacent[1]);
        if (BlockUtil.isReplaceable(tOffsetPos2))
        {
            // ensure space to place wither head
            if (BlockUtil.isNotAir(tOffsetPos2.up()))
            {
                return null;
            }
            posList.add(tOffsetPos2);
        }

        return posList;
    }

    private List<BlockPos> getTTop(final BlockPos origin)
    {
        final BlockPos[] adjacent = BlockUtil.getAdjacent(PlayerUtil.getFacing());
        if (adjacent == null)
        {
            return null;
        }
        final List<BlockPos> posList = new LinkedList<>();
        posList.add(origin);
        posList.add(origin.add(adjacent[0]));
        posList.add(origin.add(adjacent[1]));
        // if the position above the block ins't replaceable (cant place a wither head)
        posList.removeIf((pos) -> BlockUtil.isNotAir(pos.up()));
        return posList;
    }

    private int getWitherSkullSlot()
    {
        for (int i = 0; i < 9; ++i)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null
                    && itemStack.getItem() instanceof ItemSkull
                    && itemStack.getItemDamage() == WITHER_HEAD_DAMAGE)
            {
                // requires at the very least 3 wither heads
                if (!ItemUtil.isInfinite(itemStack) && itemStack.stackSize < 3)
                {
                    continue;
                }
                return i;
            }
        }
        return InventoryUtil.INVALID_SLOT;
    }
}
