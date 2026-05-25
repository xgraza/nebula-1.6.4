/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.client.cheat.impl.world;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import us.nebula.client.Nebula;
import us.nebula.client.interaction.InteractionManager;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.math.Timer;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.listener.event.network.EventPacket;
import us.nebula.client.listener.event.render.EventRender3D;
import us.nebula.client.util.player.ChatUtil;
import us.nebula.client.util.player.InventoryUtil;
import us.nebula.client.util.player.PlayerUtil;
import us.nebula.client.util.render.RenderUtil;
import us.nebula.client.util.world.BlockUtil;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CheatManifest(name = "Scaffold",
        description = "Automatically places blocks under you to give the appearance of flying",
        category = CheatCategory.WORLD)
public final class ScaffoldCheat extends Cheat
{
    @CheatInstance
    public static ScaffoldCheat INSTANCE;

    private final Setting<Double> extend = new Setting<>(
            "Extend", 0.0, 0.0, 6.0, 0.5);
    private final Setting<Boolean> towerSetting = new Setting<>(
            "Tower", true);
    private final Setting<Boolean> keeepYSetting = new Setting<>(
            "Keep Y", false);
    private final Setting<Boolean> renderSetting = new Setting<>(
            "Render", false);

    private final Timer towerTimer = new Timer();
    private double basePosY;
    private BlockData blockData;
    private int towerTicks;

    @Override
    public void onDisable()
    {
        super.onDisable();
        blockData = null;
        basePosY = -1.0;
        towerTicks = 0;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        final int slot = InventoryUtil.getHotbarSlot(
                InventoryUtil.BLOCK_FILTER);
        if (slot == -1)
        {
            return;
        }

        blockData = getBlockData();
        if (blockData == null)
        {
            return;
        }

        Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        final boolean result = InteractionManager.INSTANCE.rightClickBlock(
                blockData.pos, blockData.facing, true);
        Nebula.INSTANCE.getInventoryManager().syncSlot();
        if (!result)
        {
            return;
        }

        if (MC.gameSettings.keyBindJump.pressed && towerSetting.getValue())
        {

            if (towerTimer.hasElapsed(800L))
            {
                towerTicks = 0;
                towerTimer.resetTime();
                MC.thePlayer.motionY = -0.7f;
                return;
            }

            ++towerTicks;
            if (/*MC.thePlayer.onGround ||*/ MC.thePlayer.motionY == 0.16477328182606651)
            {
                double factor = 1-Math.min(1, towerTimer.getTimeElapsedMS() / 850L);
                //ChatUtil.sendNebula("f: " + factor);
                MC.thePlayer.motionX *= 0.88;
                MC.thePlayer.motionZ *= 0.88;
                MC.thePlayer.motionY = 0.42f;
            }
        } else
        {
            towerTicks = 0;
        }
    };

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (!renderSetting.getValue() || blockData == null)
        {
            return;
        }

        final AxisAlignedBB aabb = new AxisAlignedBB(Vec3.createVectorHelper(
                blockData.pos.getX(), blockData.pos.getY(), blockData.pos.getZ()), 1);

        RenderUtil.filledBox3D(aabb, 0, 0x80FF0000);
        RenderUtil.outlinedBox3D(aabb, 1.5f, 0xFFFF0000);
    };

    // @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S08PacketPlayerPosLook)
        {
            ChatUtil.sendNebula("Ticks: " + towerTicks);
        }
    };

    private BlockData getBlockData()
    {
        double minY = MC.thePlayer.boundingBox.minY;
        // if we're on ground and our remainder is not 0.0 (ex: 0.875 on ender chests)
        if (minY % 0.015625 == 0.0 && minY % 1.0 != 0.0)
        {
            minY += 1.0 - (minY % 1.0);
        }

        if (!keeepYSetting.getValue()
                || (towerSetting.getValue() && MC.gameSettings.keyBindJump.pressed)
                || basePosY == -1.0)
        {
            basePosY = minY - 1.0;
        }

        if (basePosY > 256)
        {
            basePosY = 256;
        }

        BlockPos pos = PlayerUtil.getOrigin(basePosY);
        if (extend.getValue() > 0.0 && !MC.gameSettings.keyBindJump.pressed)
        {
            final float yaw = MC.thePlayer.rotationYaw * 0.017453292f;

            double distance = 0.0;
            while (distance <= extend.getValue())
            {
                distance += extend.getScale().doubleValue();
                final BlockPos extendedPos = pos.add(new BlockPos(
                        (int) (-Math.sin(yaw) * distance),
                        0, (int) (Math.cos(yaw) * distance)));
                if (BlockUtil.isReplaceable(extendedPos))
                {
                    pos = extendedPos;
                    break;
                }
            }
        }

        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos neighbor = BlockUtil.offset(pos, facing);
            if (!BlockUtil.isReplaceable(neighbor) && canPlace(neighbor))
            {
                return new BlockData(neighbor, BlockUtil.getOpposite(facing));
            }
        }

        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos neighbor = BlockUtil.offset(pos, facing);
            if (BlockUtil.isReplaceable(neighbor))
            {
                for (final EnumFacing side : EnumFacing.values())
                {
                    final BlockPos n = BlockUtil.offset(neighbor, side);
                    if (!BlockUtil.isReplaceable(n) && canPlace(n))
                    {
                        return new BlockData(n, BlockUtil.getOpposite(side));
                    }
                }
            }
        }
        return null;
    }

    private boolean canPlace(final BlockPos pos)
    {
        return pos.getY() <= 256;
    }

    private static final class BlockData
    {
        private final BlockPos pos;
        private final EnumFacing facing;

        public BlockData(BlockPos pos, EnumFacing facing)
        {
            this.pos = pos;
            this.facing = facing;
        }
    }
}
