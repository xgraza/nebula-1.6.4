package ez.nebula.client.impl.module.world;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.input.EventUpdateInput;
import ez.nebula.client.api.listener.event.render.EventRender3D;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.api.setting.block.BlockSetting;
import ez.nebula.client.api.setting.block.BlockValue;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.impl.module.combat.AutoBedModule;
import ez.nebula.client.impl.module.combat.KillAuraModule;
import ez.nebula.client.impl.module.player.AutoEatModule;
import ez.nebula.client.util.math.AngleUtil;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.minecraft.world.BlockInfo;
import ez.nebula.client.util.minecraft.world.BlockUtil;
import ez.nebula.client.util.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.init.Blocks;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 06/26/26
 */
@ModuleManifest(name = "AutoHighway",
        description = "Automatically builds a walkable highway",
        category = ModuleCategory.WORLD)
public final class AutoHighwayModule extends Module
{
    private final Setting<BlockValue> blockSetting = blockBuilder("Block")
            .setBlock(Blocks.obsidian)
            .setDescription("The kind of block to make a highway with")
            .build();
    private final Setting<Double> rangeSetting = numberBuilder("Range", 4.5)
            .setMin(1.0)
            .setMax(6.0)
            .setScale(0.1)
            .setDescription("The range to break and place at")
            .build();
    private final Setting<Boolean> excavateSetting = builder("Excavate", true)
            .setDescription("If to mine out space")
            .build();
    private final Setting<Integer> excavateHeightSetting = numberBuilder("Excavate Height", 3)
            .setMin(2)
            .setMax(5)
            .setScale(1)
            .setDescription("The height from the player origin to mine")
            .build();
    private final Setting<Boolean> onlyBlockSetting = builder("Mine non selected", false)
            .setDescription("If when excavating to mine blocks on highway path positions that are not the selected block type")
            .build();
    private final Setting<Integer> blocksPerTickSetting = numberBuilder("Blocks per Tick", 3)
            .setMin(1)
            .setMax(20)
            .setScale(1)
            .setDescription("How many blocks to place per tick")
            .build();
    private final Setting<Integer> sizeSetting = numberBuilder("Size", 1)
            .setMin(1)
            .setMax(3)
            .setScale(1)
            .setDescription("The width in blocks on each side for the highway")
            .build();
    private final Setting<Integer> lengthSetting = numberBuilder("Length", 4)
            .setMin(1)
            .setMax(6)
            .setScale(1)
            .setDescription("The length of highway to build ahead of you")
            .build();
    private final Setting<Boolean> autoWalkSetting = builder("Auto Walk", false)
            .setDescription("If to automatically walk when building a highway")
            .build();
    private final Setting<Boolean> preserveSignsSetting = builder("Preserve Signs", false)
            .setDescription("If to attempt to not break signs")
            .build();

    private List<BlockPos> highwayPositionList;

    private BlockInfo breakInfo;
    private int prevSlot = -1;
    private boolean walk;

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (MC.thePlayer != null)
        {
            if (prevSlot != -1)
            {
                MC.thePlayer.inventory.currentItem = prevSlot;
            } else
            {
                Nebula.INSTANCE.getInventoryManager().syncSlot();
            }
            if (walk)
            {
                MC.thePlayer.movementInput.moveForward = 0.0f;
            }
        }
        if (breakInfo != null && MC.theWorld != null)
        {
            MC.playerController.resetBlockRemoving();
        }
        prevSlot = -1;
        breakInfo = null;
        walk = false;
        if (highwayPositionList != null)
        {
            highwayPositionList.clear();
        }
        PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
    }

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
//        for (final BlockPos pos : highwayPositionList)
//        {
//            final AxisAlignedBB aabb = new AxisAlignedBB(Vec3.createVectorHelper(
//                    pos.getX(), pos.getY(), pos.getZ()), 1);
//
//            RenderUtil.renderFilledAABB(aabb, QuadMask.ALL_FACES, 0x8000FF00);
//            RenderUtil.renderOutlinedAABB(aabb, 1.5f, QuadMask.ALL_FACES, 0xFF00FF00);
//        }

        if (breakInfo == null)
        {
            return;
        }
        final AxisAlignedBB aabb = new AxisAlignedBB(Vec3.createVectorHelper(
                breakInfo.getPos().getX(), breakInfo.getPos().getY(), breakInfo.getPos().getZ()), 1);

        RenderUtil.renderFilledAABB(aabb, RenderUtil.calculateFaceMask(breakInfo.getFacing()), 0x80FF0000);
        RenderUtil.renderOutlinedAABB(aabb, 1.5f, RenderUtil.calculateFaceMask(breakInfo.getFacing()), 0xFFFF0000);
    };

    @Subscribe
    private final EventListener<EventUpdateInput> updateInputEventListener = event ->
    {
        if (autoWalkSetting.getValue()
                // imagine dying because autohighway wouldnt let you run lol
                && !KillAuraModule.INSTANCE.isAttacking() && !AutoBedModule.INSTANCE.isActive())
        {
            event.getInput().moveForward = walk ? 1.0f : 0.0f;
        }
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (AutoEatModule.INSTANCE.isActive() || KillAuraModule.INSTANCE.isAttacking() || AutoBedModule.INSTANCE.isActive())
        {
            prevSlot = -1;
            return;
        }

        highwayPositionList = getHighwayPositions();
        if (highwayPositionList.isEmpty())
        {
            walk = false;
            return;
        }

        final List<BlockInfo> excavatePositionList = getExcavatePositions(highwayPositionList);
        // break blocks first, can't place blocks where theres already blocks, duh!
        if (!excavatePositionList.isEmpty() && excavateSetting.getValue())
        {
            walk = false;
            excavate(excavatePositionList);
            return;
        } else
        {
            if (breakInfo != null)
            {
                MC.playerController.resetBlockRemoving();
                breakInfo = null;
            }
            // nothing to break, give control back
            PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
            walk = false;

            if (prevSlot != -1)
            {
                MC.thePlayer.inventory.currentItem = prevSlot;
                prevSlot = -1;
                return; // wait a tick before trying to place again
            }
        }

        final int slot = InventoryUtil.getSlot(0, 9,
                (stack) -> ((BlockSetting) blockSetting).isBlock(stack));
        if (slot == -1)
        {
            walk = false;
            return;
        }

        walk = true;

        int blocksPlaced = 0;
        for (final BlockPos highwayPos : highwayPositionList)
        {
            if (!BlockUtil.isReplaceable(highwayPos))
            {
                continue;
            }
            final BlockInfo info = BlockUtil.getPlacement(highwayPos);
            if (info != null)
            {
                Nebula.INSTANCE.getInventoryManager().setSlot(slot);
                if (InteractionManager.INSTANCE.rightClickBlock(info.getPos(), info.getFacing(), true))
                {
                    ++blocksPlaced;
                }
            }

            if (blocksPlaced >= blocksPerTickSetting.getValue())
            {
                break;
            }
        }

        if (blocksPlaced > 0)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
    };

    private void excavate(final List<BlockInfo> excavatePosList)
    {
        if (breakInfo != null)
        {
            final BlockPos pos = breakInfo.getPos();
            if (MC.thePlayer.getDistance(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5)
                        > rangeSetting.getValue()
                    || BlockUtil.isReplaceable(pos))
            {
                MC.playerController.resetBlockRemoving();
                breakInfo = null;
            } else
            {
                swapToBestBlockSlot(pos);
                if (InteractionManager.INSTANCE.breakBlock(pos, breakInfo.getFacing()))
                {
                    MC.playerController.resetBlockRemoving();
                    breakInfo = null;
                } else
                {
                    return;
                }
            }
        }

        final BlockInfo info = excavatePosList.get(0);
        swapToBestBlockSlot(info.getPos());
        InteractionManager.INSTANCE.breakBlock(info.getPos(), info.getFacing());
        breakInfo = info;
    }

    private void swapToBestBlockSlot(final BlockPos pos)
    {
        final int slot = InventoryUtil.getBestToolSlotFor(MC.theWorld.getBlock(pos));
        if (slot != -1)
        {
            if (prevSlot == -1)
            {
                prevSlot = MC.thePlayer.inventory.currentItem;
            }
            MC.thePlayer.inventory.currentItem = slot;
        }
    }

    private List<BlockInfo> getExcavatePositions(final List<BlockPos> highwayPosList)
    {
        final int maxY = MathHelper.floor_double(MC.thePlayer.boundingBox.minY) + excavateHeightSetting.getValue();
        final List<BlockInfo> posList = new LinkedList<>();
        for (final BlockPos highwayPos : highwayPosList)
        {
            Block block = MC.theWorld.getBlock(highwayPos);
            if (((BlockSetting) blockSetting).getBlock() != block
                    && block.blockHardness != -1.0f
                    && !BlockUtil.isReplaceable(highwayPos)
                    && onlyBlockSetting.getValue())
            {
                final BlockInfo info = getBreakInfo(highwayPos, block);
                if (info != null)
                {
                    posList.add(info);
                }
            }

            for (int y = 1; y <= excavateHeightSetting.getValue() + 1; ++y)
            {
                if (highwayPos.getY() + y > maxY)
                {
                    continue;
                }
                final BlockPos pos = highwayPos.offset(EnumFacing.UP, y);
                block = MC.theWorld.getBlock(pos);
                if (!block.getMaterial().isReplaceable() && block.blockHardness != -1.0f)
                {
                    final BlockInfo info = getBreakInfo(pos, block);
                    if (info != null)
                    {
                        posList.add(info);
                    }
                }
            }
        }
        if (!posList.isEmpty())
        {
            posList.sort(Comparator.comparingDouble((info) ->
                    MC.thePlayer.getDistance(
                            info.getPos().getX() + 0.5, info.getPos().getY() + 1.0, info.getPos().getZ() + 0.5)));
        }
        return posList;
    }

    private BlockInfo getBreakInfo(final BlockPos pos, final Block block)
    {
        if (MC.thePlayer.getDistance(
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5) > rangeSetting.getValue())
        {
            return null;
        }
        if (preserveSignsSetting.getValue())
        {
            if (block instanceof BlockSign)
            {
                return null;
            }
            for (final EnumFacing face : EnumFacing.values())
            {
                final BlockPos neighbor = pos.offset(face);
                if (MC.theWorld.getBlock(neighbor) instanceof BlockSign)
                {
                    return null;
                }
            }
        }
        final EnumFacing face = AngleUtil.getVisibleFace(pos, 6.0);
        if (face == null)
        {
            return null;
        }
        return new BlockInfo(pos, face);
    }

    private List<BlockPos> getHighwayPositions()
    {
        final List<BlockPos> highwayPositionList = new LinkedList<>();
        final EnumFacing facing = PlayerUtil.getFacing();
        final BlockPos[] adjacent = BlockUtil.getAdjacent(facing);
        if (adjacent == null)
        {
            return highwayPositionList;
        }

        for (int b = 0; b < lengthSetting.getValue() + 1; ++b)
        {
            final BlockPos origin = PlayerUtil.getOrigin().offset(facing, b).down();
            highwayPositionList.add(origin);
            for (final BlockPos adjacentPos : adjacent)
            {
                for (int i = 0; i < sizeSetting.getValue(); ++i)
                {
                    highwayPositionList.add(origin.add(adjacentPos.getX() * (i + 1),
                            0, adjacentPos.getZ() * (i + 1)));
                }

                // this will now add our "rails"
                highwayPositionList.add(origin.add(adjacentPos.getX() * (sizeSetting.getValue() + 1),
                        1, adjacentPos.getZ() * (sizeSetting.getValue() + 1)));
            }
        }

        return highwayPositionList;
    }
}
