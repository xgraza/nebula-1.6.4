/*
 * Copyright (c) xgraza 2025
 */

package ez.nebula.client.impl.module.world;

import ez.nebula.client.api.setting.BindSetting;
import ez.nebula.client.api.setting.ColorSetting;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.impl.module.render.HUDModule;
import ez.nebula.client.util.math.AngleUtil;
import ez.nebula.client.util.minecraft.world.BlockInfo;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.player.EventSafeWalk;
import ez.nebula.client.api.listener.event.render.EventRender2D;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.math.Timer;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.api.listener.event.render.EventRender3D;
import ez.nebula.client.util.minecraft.player.ChatUtil;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.ItemUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.render.RenderUtil;
import ez.nebula.client.api.render.font.Fonts;
import ez.nebula.client.util.minecraft.world.BlockUtil;
import org.lwjgl.input.Keyboard;

/**
 * @author xgraza
 * @since 02/16/25
 */
@ModuleManifest(name = "Scaffold",
        description = "Automatically places blocks under you to give the appearance of flying",
        category = ModuleCategory.WORLD)
public final class ScaffoldModule extends Module
{
    @ModuleInstance
    public static ScaffoldModule INSTANCE;

    private static final int SCAFFOLD_ROTATION_PRIORITY = 50;

    private final NumberSetting<Double> extend = numberBuilder("Extend", 0.0)
            .setMin(0.0)
            .setMax(6.0)
            .setScale(0.5)
            .setDescription("How far to extend forward")
            .build();
    private final Setting<Boolean> towerSetting = builder("Tower", true)
            .setDescription("If to allow quicker upwards movement")
            .build();
    private final Setting<Boolean> keeepYSetting = builder("Keep Y", false)
            .setDescription("If to keep your original y-level when scaffolding")
            .build();
    private final Setting<Boolean> safeWalkSetting = builder("SafeWalk", false)
            .setDescription("If to use safe walk")
            .build();
    private final Setting<Boolean> rotateSetting = builder("Rotate", false)
            .setDescription("If to rotate towards the block you're placing")
            .build();
    private final Setting<Boolean> renderSetting = builder("Render", false)
            .setDescription("If to render where the block is being placed")
            .build();
    private final BindSetting downwardsSetting = bindBuilder("Downwards")
            .setKeyCode(Keyboard.KEY_NONE)
            .setDescription("The key to press to toggle downwards scaffold")
            .build();
    private final Setting<Boolean> prioritizeHeldSetting = builder("Prioritize Held", true)
            .setDescription("If to prioritize the block item held in your hand")
            .build();

    private final Timer towerTimer = new Timer();
    private double basePosY;
    private BlockInfo blockData;
    private int towerTicks, slot;
    private float[] angles;

    @Override
    public void onEnable()
    {
        super.onEnable();
        downwardsSetting.getValue().setState(false);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        blockData = null;
        basePosY = -1.0;
        towerTicks = 0;
        slot = -1;
        angles = null;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        slot = InventoryUtil.getHotbarSlot(this::isStackValid);
        if (prioritizeHeldSetting.getValue() && isStackValid(MC.thePlayer.getHeldItem()))
        {
            slot = MC.thePlayer.inventory.currentItem;
        }
        if (slot == -1)
        {
            return;
        }

        blockData = getBlockData();
        if (blockData == null)
        {
            return;
        }

        if (rotateSetting.getValue())
        {
            if (angles == null)
            {
                return;
            }
            if (!Nebula.INSTANCE.getRotationManager().spoof(angles[0], angles[1], SCAFFOLD_ROTATION_PRIORITY))
            {
                return;
            }
        }

        Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        final boolean result = InteractionManager.INSTANCE.rightClickBlock(
                blockData.getPos(), blockData.getFacing(), true);
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
    private final EventListener<EventSafeWalk> eventSafeWalkEventListener = event ->
    {
        if (MC.thePlayer.onGround && !MC.gameSettings.keyBindJump.pressed && safeWalkSetting.getValue())
        {
            event.cancel();
        }
    };

    @Subscribe
    private final EventListener<EventRender2D> render2DEventListener = event ->
    {
        if (slot == -1 || !renderSetting.getValue())
        {
            return;
        }
        final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(slot);
        if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock))
        {
            return;
        }

        int blocksLeft = 0;
        if (ItemUtil.isInfinite(itemStack))
        {
            blocksLeft = -1;
        } else
        {
            final int stackSize = itemStack.stackSize;
            if (stackSize != 0)
            {
                blocksLeft = stackSize;
            }
        }

        final String text = String.format("%sBlocks left:%s %s",
                EnumChatFormatting.GRAY,
                EnumChatFormatting.RESET,
                blocksLeft == -1 ?
                        EnumChatFormatting.RED + "Infinite"
                        : blocksLeft);
        final double totalWidth = 16 + 2 + Fonts.POPPINS.getStringWidth(text);
        final double posX = event.getResolution().getScaledWidth_double() / 2.0 - totalWidth / 2.0;
        final double posY = event.getResolution().getScaledHeight_double() / 2.0 + 100;

        RenderUtil.renderRectangle(posX - 2, posY - 2, totalWidth + 4, 16 + 4, 0x80000000);
        RenderUtil.renderItemWithoutEffects(itemStack, (int) posX, (int) posY);
        Fonts.POPPINS.drawStringShadow(text, posX + 18, posY + 2, -1);
    };

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (blockData == null)
        {
            return;
        }

        if (rotateSetting.getValue())
        {
            angles = AngleUtil.anglesToBlock(blockData.getPos(), blockData.getFacing(), event.getPartialTicks());
        }

        if (!renderSetting.getValue())
        {
            return;
        }

        final AxisAlignedBB aabb = new AxisAlignedBB(blockData.getPos());
        final ColorSetting cs = (ColorSetting) HUDModule.INSTANCE.primaryColorSetting;
        RenderUtil.renderFilledAABB(aabb, RenderUtil.calculateFaceMask(blockData.getFacing()), cs.getValueInt(120));
        RenderUtil.renderOutlinedAABB(aabb, 1.5f, RenderUtil.calculateFaceMask(blockData.getFacing()), cs.getValueInt());
    };

    // @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S08PacketPlayerPosLook)
        {
            ChatUtil.sendNebula("Ticks: " + towerTicks);
        }
    };

    private boolean isStackValid(final ItemStack stack)
    {
        return stack != null
                && stack.getItem() instanceof ItemBlock
                && ((ItemBlock) stack.getItem()).getBlock().getMaterial().isSolid();
    }

    private BlockInfo getBlockData()
    {
        double minY = MC.thePlayer.boundingBox.minY;
        // if we're on ground and our remainder is not 0.0 (ex: 0.875 on ender chests)
        if (minY % 0.015625 == 0.0 && minY % 1.0 != 0.0)
        {
            minY += 1.0 - (minY % 1.0);
        }

        if (!keeepYSetting.getValue()
                || (towerSetting.getValue() && MC.gameSettings.keyBindJump.pressed)
                || basePosY == -1.0
                || downwardsSetting.getValue().isToggled())
        {
            basePosY = minY - 1.0;
        }

        if (basePosY > 256)
        {
            basePosY = 256;
        }

        if (downwardsSetting.getValue().isToggled() && !MC.gameSettings.keyBindJump.pressed)
        {
            basePosY -= 1;
        }

        BlockPos pos = PlayerUtil.getOrigin(basePosY);
        if (extend.getValue() > 0.0 && !MC.gameSettings.keyBindJump.pressed)
        {
            final float yaw = MC.thePlayer.rotationYaw * 0.017453292f;

            double distance = 0.0;
            while (distance <= extend.getValue())
            {
                distance += 0.5;
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
        return BlockUtil.getPlacement(pos);
    }
}
