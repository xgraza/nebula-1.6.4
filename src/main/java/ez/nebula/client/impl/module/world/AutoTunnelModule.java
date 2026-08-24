/*
 * Copyright (c) xgraza 2025
 */

package ez.nebula.client.impl.module.world;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.input.EventUpdateInput;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.impl.module.combat.AutoBedModule;
import ez.nebula.client.impl.module.combat.KillAuraModule;
import ez.nebula.client.impl.module.player.AutoEatModule;
import ez.nebula.client.util.math.AngleUtil;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.minecraft.world.BlockInfo;
import ez.nebula.client.util.minecraft.world.BlockUtil;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.item.ItemBlock;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author xgraza
 * @since 04/18/25
 */
@ModuleManifest(name = "AutoTunnel",
        description = "Automatically digs a tunnel in front of you",
        category = ModuleCategory.WORLD)
public final class AutoTunnelModule extends Module
{
    private final NumberSetting<Integer> lengthSetting = numberBuilder("Length", 4)
            .setMin(1)
            .setMax(6)
            .setScale(1)
            .setDescription("The length of the tunnel to dig ahead of you")
            .build();
    private final NumberSetting<Integer> blocksPerTickSetting = numberBuilder("Blocks per Tick", 3)
            .setMin(1)
            .setMax(20)
            .setScale(1)
            .setDescription("How many blocks to break per tick")
            .build();
    private final Setting<Boolean> autoWalkSetting = builder("Auto Walk", false)
            .setDescription("If to automatically walk when tunneling")
            .build();
    private final Setting<Boolean> backplaceSetting = builder("Backplace", false)
            .setDescription("If to replace the blocks behind you")
            .build();

    private final Set<BlockPos> replaceQueue = new ConcurrentSet<>();
    private BlockInfo breakInfo;
    private int prevSlot = InventoryUtil.INVALID_SLOT;
    private boolean walk;

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (prevSlot != -1)
        {
            Nebula.INSTANCE.getInventoryManager().setSlotClient(prevSlot);
        }
        prevSlot = InventoryUtil.INVALID_SLOT;
        replaceQueue.clear();
        breakInfo = null;
        walk = false;
    }

    @Subscribe
    private final EventListener<EventUpdateInput.Post> postEventListener = event ->
    {
        if (autoWalkSetting.getValue() && walk && event.getInput().equals(MC.thePlayer.movementInput))
        {
            event.getInput().sneak = false;
            event.getInput().moveForward = 1;
        }
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (KillAuraModule.INSTANCE.isAttacking() || AutoBedModule.INSTANCE.isActive() || AutoEatModule.INSTANCE.isActive())
        {
            walk = false;
            return;
        }

        walk = true;

        if (breakInfo != null)
        {
            swapToBestBlockSlot(breakInfo.getPos());
            if (InteractionManager.INSTANCE.breakBlock(breakInfo.getPos(), breakInfo.getFacing()))
            {
                walk = true;
                if (backplaceSetting.getValue())
                {
                    replaceQueue.add(breakInfo.getPos());
                }
                breakInfo = null;
                swapBack();
            }
            walk = false;
            return;
        }

        if (!replaceQueue.isEmpty() && backplaceSetting.getValue())
        {
            walk = true;
            int blocks = 0;
            for (BlockPos pos : replaceQueue)
            {
                if (!isBlockBehindPlayer(pos))
                {
                    continue;
                }
                final int slot = InventoryUtil.getHotbarSlot(
                        (stack) -> stack.getItem() instanceof ItemBlock
                                && ((ItemBlock) stack.getItem()).getBlock().getMaterial().isSolid());
                if (slot == InventoryUtil.INVALID_SLOT)
                {
                    break;
                }
                final BlockInfo info = BlockUtil.getPlacement(pos);
                if (info == null)
                {
                    continue;
                }
                Nebula.INSTANCE.getInventoryManager().setSlot(slot);
                if (InteractionManager.INSTANCE.rightClickBlock(info.getPos(), info.getFacing()))
                {
                    replaceQueue.remove(pos);
                    ++blocks;
                }
                Nebula.INSTANCE.getInventoryManager().syncSlot();
                if (blocks >= blocksPerTickSetting.getValue())
                {
                    return;
                }
            }
            if (blocks > 0)
            {
                return;
            }
        }
        final List<BlockPos> tunnelBlockList = getTunnelBlockList();
        int blocks = 0;
        for (final BlockPos pos : tunnelBlockList)
        {
            final BlockInfo info = getBreakInfo(pos);
            if (info == null)
            {
                continue;
            }
            swapToBestBlockSlot(pos);
            walk = false;
            if (InteractionManager.INSTANCE.breakBlock(pos, info.getFacing()))
            {
                walk = true;
                swapBack();
                ++blocks;
                if (backplaceSetting.getValue())
                {
                    replaceQueue.add(pos);
                }
                if (blocksPerTickSetting.getValue() <= blocks)
                {
                    break;
                }
            } else
            {
                walk = false;
                // me must continue to break on the next tick
                breakInfo = info;
                return;
            }
        }
    };

    private void swapToBestBlockSlot(final BlockPos pos)
    {
        final int slot = InventoryUtil.getBestToolSlotFor(MC.theWorld.getBlock(pos));
        if (slot != InventoryUtil.INVALID_SLOT)
        {
            if (prevSlot == InventoryUtil.INVALID_SLOT)
            {
                prevSlot = MC.thePlayer.inventory.currentItem;
            }
            Nebula.INSTANCE.getInventoryManager().setSlotClient(slot);
        }
    }

    private void swapBack()
    {
        if (prevSlot != InventoryUtil.INVALID_SLOT && MC.thePlayer != null)
        {
            Nebula.INSTANCE.getInventoryManager().setSlotClient(prevSlot);
        }
        prevSlot = InventoryUtil.INVALID_SLOT;
    }

    private BlockInfo getBreakInfo(final BlockPos pos)
    {
        final EnumFacing face = AngleUtil.getVisibleFace(pos, 6.0);
        if (face == null)
        {
            return null;
        }
        return new BlockInfo(pos, face);
    }

    private List<BlockPos> getTunnelBlockList()
    {
        final List<BlockPos> blockPosList = new LinkedList<>();
        final BlockPos origin = PlayerUtil.getOrigin();
        final EnumFacing facing = PlayerUtil.getFacing();
        for (int i = 1; i < lengthSetting.getValue() + 1; ++i)
        {
            BlockPos pos = origin.offset(facing, i);
            if (BlockUtil.isNotAir(pos) && MC.theWorld.getBlock(pos).blockHardness != -1.0f)
            {
                blockPosList.add(pos);
            }
            pos = pos.up();
            if (BlockUtil.isNotAir(pos) && MC.theWorld.getBlock(pos).blockHardness != -1.0f)
            {
                blockPosList.add(pos);
            }
        }
        return blockPosList;
    }

    private boolean isBlockBehindPlayer(final BlockPos pos)
    {
        final BlockPos vec = BlockUtil.getFacingVec(PlayerUtil.getFacing());
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
}
