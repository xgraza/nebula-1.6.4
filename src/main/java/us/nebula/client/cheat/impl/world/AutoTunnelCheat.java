/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.client.cheat.impl.world;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import us.nebula.client.Nebula;
import us.nebula.client.interaction.InteractionManager;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.setting.Setting;
import us.nebula.client.util.render.QuadMask;
import us.nebula.client.cheat.impl.combat.KillAuraCheat;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.listener.event.input.EventUpdateInput;
import us.nebula.client.listener.event.render.EventRender3D;
import us.nebula.client.util.player.InventoryUtil;
import us.nebula.client.util.player.PlayerUtil;
import us.nebula.client.util.render.RenderUtil;
import us.nebula.client.util.world.BlockInfo;
import us.nebula.client.util.world.BlockUtil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author xgraza
 * @since 04/18/25
 */
@CheatManifest(name = "AutoTunnel",
        description = "Automatically digs a tunnel in front of you",
        category = CheatCategory.WORLD)
public final class AutoTunnelCheat extends Cheat
{
    private final Setting<Integer> blocksSetting = numberBuilder("Blocks", 2)
            .setMin(1)
            .setMax(4)
            .setScale(1)
            .setDescription("How many blocks ahead to break")
            .build();
    private final Setting<Boolean> keepYSetting = builder("Keep Y", true)
            .setDescription("If to keep your original y-level when tunneling")
            .build();
    private final Setting<Boolean> replaceLavaSetting = builder("Replace Lava", true)
            .setDescription("If to automatically place blocks in random lava holes in the nether")
            .build();
    private final Setting<Boolean> backPlaceSetting = builder("Back Fill", false)
            .setDescription("If to automatically fill the broken tunnel behind you")
            .build();
    private final Setting<Boolean> autoWalkSetting = builder("Auto Walk", false)
            .setDescription("If to automatically move forward")
            .build();
    private final Setting<Boolean> renderSetting = builder("Render", true)
            .setDescription("If to render break and placements")
            .build();

    private final Queue<BlockInfo> blockBreakQueue = new ConcurrentLinkedQueue<>();
    private final Queue<BlockInfo> backPlaceQueue = new ConcurrentLinkedQueue<>();
    private BlockInfo currentBlock;

    private int posY = -1;
    private boolean moveForward;

    @Override
    public void onDisable()
    {
        super.onDisable();
        blockBreakQueue.clear();
        backPlaceQueue.clear();
        if (currentBlock != null && MC.playerController.sameToolAndBlock(
                currentBlock.getPos().getX(),
                currentBlock.getPos().getY(),
                currentBlock.getPos().getZ()))
        {
            MC.playerController.resetBlockRemoving();
        }
        if (MC.thePlayer != null)
        {
            if (autoWalkSetting.getValue())
            {
                MC.thePlayer.movementInput.moveForward = 0;
            }
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
        moveForward = false;
        currentBlock = null;
        posY = -1;
        PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
    }

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (!renderSetting.getValue() || currentBlock == null)
        {
            return;
        }

        RenderUtil.renderFilledAABB(new AxisAlignedBB(currentBlock.getPos()), QuadMask.ALL_FACES, 0x8000FF00);
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        // do not interfere, we may be trying to kill a creeper or something...
        if (KillAuraCheat.INSTANCE.isAttacking() || KillAuraCheat.INSTANCE.isBlocking())
        {
            return;
        }

        if (blockBreakQueue.isEmpty() && backPlaceSetting.getValue())
        {
            final List<BlockInfo> replaceBlockList = new LinkedList<>();
            for (final BlockInfo info : backPlaceQueue)
            {
                if (isBlockBehindPlayer(info.getPos()))
                {
                    replaceBlockList.add(info);
                }
            }

            moveForward = false;
            if (replaceBlocks(replaceBlockList))
            {
                // remove all the replaced blocks
                for (final BlockInfo info : replaceBlockList)
                {
                    if (!BlockUtil.isReplaceable(info.getPos()))
                    {
                        backPlaceQueue.remove(info);
                    }
                }
                return;
            }
            moveForward = true;
        }

        searchForBlocks();
        if (currentBlock == null)
        {
            if (blockBreakQueue.isEmpty())
            {
                return;
            }
            currentBlock = blockBreakQueue.poll();
            return;
        }

        moveForward = blockBreakQueue.size() < 4;

        if (replaceLavaSetting.getValue())
        {
            // check if the block at this position is lava, so we can place a block there and then replace...
            final List<BlockInfo> lavaHolePositions = getLavaFillPositions(currentBlock.getPos());
            if (replaceBlocks(lavaHolePositions))
            {
                return;
            }
        }

        if (!PacketMineCheat.INSTANCE.isToggled())
        {
            final int slot = InventoryUtil.getBestToolSlotFor(MC.theWorld.getBlock(currentBlock.getPos()));
            if (slot != -1 && MC.thePlayer.inventory.currentItem != slot)
            {
                MC.playerController.resetBlockRemoving();
                MC.thePlayer.inventory.currentItem = slot;
                return;
            }
        }
        if (InteractionManager.INSTANCE.breakBlock(currentBlock.getPos(), currentBlock.getFacing()))
        {
            if (backPlaceSetting.getValue())
            {
                backPlaceQueue.add(currentBlock);
            }
            currentBlock = null;
        }
    };

    @Subscribe
    private final EventListener<EventUpdateInput> updateInputEventListener = event ->
    {
        if (autoWalkSetting.getValue() && moveForward)
        {
            // allow player to stop movement if they sneak
            if (event.getInput().sneak)
            {
                return;
            }
            // go forward, do not strafe
            event.getInput().moveForward = 1;
            event.getInput().moveStrafe = 0;
        }
    };

    private boolean isBlockBehindPlayer(final BlockPos pos)
    {
        final EnumFacing facing = PlayerUtil.getFacing();
        final BlockPos vec = BlockUtil.getFacingVec(facing);

        int delta = 0;
        int axis = 0;
        if (vec.getX() != 0)
        {
            delta = MathHelper.floor_double(MC.thePlayer.posX) - pos.getX();
            axis = vec.getX();
        }
        if (vec.getZ() != 0)
        {
            delta = MathHelper.floor_double(MC.thePlayer.posZ) - pos.getZ();
            axis = vec.getZ();
        }
        return axis > 0 ? delta > 0 : delta < 0;
    }

    private List<BlockInfo> getLavaFillPositions(final BlockPos pos)
    {
        final List<BlockInfo> blockInfoList = new LinkedList<>();
        Block block = MC.theWorld.getBlock(pos);
        if (block == Blocks.lava || block == Blocks.flowing_lava)
        {
            final BlockInfo info = getBlockInfo(pos);
            if (info != null)
            {
                blockInfoList.add(info);
            }
        }
        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos n = pos.offset(facing);
            block = MC.theWorld.getBlock(n);
            if (block == Blocks.lava || block == Blocks.flowing_lava)
            {
                final BlockInfo info = getBlockInfo(n);
                if (info != null)
                {
                    blockInfoList.add(info);
                }
            }
        }
        return blockInfoList;
    }

    private BlockInfo getBlockInfo(final BlockPos pos)
    {
        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos n = pos.offset(facing);
            if (!BlockUtil.isReplaceable(n))
            {
                return new BlockInfo(n, BlockUtil.getOpposite(facing));
            }
        }
        return null;
    }

    private void searchForBlocks()
    {
        if (!blockBreakQueue.isEmpty())
        {
            return;
        }
        if (!keepYSetting.getValue() || posY == -1)
        {
            posY = MathHelper.floor_double(MC.thePlayer.boundingBox.minY);
        }
        final BlockPos origin = PlayerUtil.getOrigin(posY);
        final EnumFacing facing = PlayerUtil.getFacing();
        final EnumFacing opposite = BlockUtil.getOpposite(facing);
        final BlockPos faceVec = BlockUtil.getFacingVec(facing);

        for (int offset = 0; offset < blocksSetting.getValue(); ++offset)
        {
            final BlockPos offsetPos = new BlockPos(
                    origin.getX() + ((offset + 1) * faceVec.getX()),
                    posY,
                    origin.getZ() + ((offset + 1) * faceVec.getZ()));
            final BlockPos abovePos = offsetPos.up();

            if (isValidBlock(abovePos))
            {
                blockBreakQueue.add(new BlockInfo(abovePos, opposite));
            }
            if (isValidBlock(offsetPos))
            {
                blockBreakQueue.add(new BlockInfo(offsetPos, opposite));
            }
        }
    }

    private boolean isValidBlock(final BlockPos pos)
    {
        final Block block = MC.theWorld.getBlock(pos);
        return block != null && !block.getMaterial().isReplaceable() && block.blockHardness != -1;
    }

    private boolean replaceBlocks(final Collection<BlockInfo> positions)
    {
        if (positions.isEmpty())
        {
            return false;
        }
        boolean placed = false;
        for (BlockInfo info : positions)
        {
            // recalculate the info for placement
            info = getBlockInfo(info.getPos());
            if (info == null)
            {
                continue;
            }

            final int slot = InventoryUtil.getHotbarSlot((stack) ->
            {
                if (!(stack.getItem() instanceof ItemBlock))
                {
                    return false;
                }
                final ItemBlock itemBlock = (ItemBlock) stack.getItem();
                return itemBlock.getBlock().blockHardness != -1;
            });
            if (slot == -1)
            {
                continue;
            }
            Nebula.INSTANCE.getInventoryManager().setSlot(slot);
            final boolean placeResult = InteractionManager.INSTANCE.rightClickBlock(
                    info.getPos(), info.getFacing());
            if (placeResult)
            {
                placed = true;
                MC.thePlayer.swingItem();
            }
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
        return placed; // wait until next tick to continue breaking
    }
}
