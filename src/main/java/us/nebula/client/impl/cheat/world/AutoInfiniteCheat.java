package us.nebula.client.impl.cheat.world;

import net.minecraft.block.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import us.nebula.client.Nebula;
import us.nebula.client.api.interaction.InteractionManager;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.util.player.InventoryUtil;
import us.nebula.client.util.world.BlockUtil;

/**
 * @author xgraza
 * @since 04/04/25
 */
@CheatManifest(name = "AutoInfinite",
        description = "Automatically creates infinite items",
        category = CheatCategory.WORLD)
public final class AutoInfiniteCheat extends Cheat
{
    private final Setting<Boolean> placeTntSetting = new Setting<>(
            "Place TNT", false);
    private final Setting<Boolean> igniteTNTSetting = new Setting<>(
            "Ignite TNT", false);

    private boolean swapItems;

    @Override
    public void onDisable()
    {
        super.onDisable();
        swapItems = false;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (swapItems)
        {
            if (!(MC.currentScreen instanceof GuiContainer))
            {
                setToggled(false);
                return;
            }
            if (MC.thePlayer.ticksExisted % 2 == 0)
            {
                MC.playerController.windowClick(MC.thePlayer.openContainer.windowId,
                        18, 0, 2, MC.thePlayer);
            }
            return;
        }

        if (!isValidItem())
        {
            Nebula.INSTANCE.getToastManager().error(
                    "AutoInfinite",
                    "First item slot needs to be a proper item",
                    7500L);
            setToggled(false);
            return;
        }

        final MovingObjectPosition result = MC.objectMouseOver;
        if (result == null
                || result.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK
                || !(MC.theWorld.getBlock(result.blockX, result.blockY, result.blockZ) instanceof BlockChest))
        {
            Nebula.INSTANCE.getToastManager().error(
                    "AutoInfinite",
                    "Look at a chest you want to use",
                    7500L);
            setToggled(false);
            return;
        }
        final BlockPos chestPos = new BlockPos(result.blockX, result.blockY, result.blockZ);
        final BlockPos tntPos = handleTNTPlacement(chestPos);
        if (tntPos == null)
        {
            return;
        }

        if (MC.theWorld.getBlock(result.blockX, result.blockY, result.blockZ) != Blocks.trapped_chest
                && !handleIgniteTNT(tntPos))
        {
            return;
        }

        if (!(MC.currentScreen instanceof GuiContainer))
        {
            // open chest
            final boolean clickResult = InteractionManager.INSTANCE.rightClickBlock(result);
            if (clickResult)
            {
                MC.thePlayer.swingItem();
            }
            return;
        }
        swapItems = true;
    };

    private boolean isPrimedTNTNear(final BlockPos blockPos)
    {
        return !MC.theWorld.getEntitiesWithinAABB(
                EntityTNTPrimed.class,
                new AxisAlignedBB(blockPos).expand(2, 2, 2)).isEmpty();
    }

    private boolean handleIgniteTNT(final BlockPos blockPos)
    {
        if (isPrimedTNTNear(blockPos))
        {
            return true;
        }
        // if there is not already primed TNT near & we do not ignite ourselves, return
        if (!igniteTNTSetting.getValue())
        {
            Nebula.INSTANCE.getToastManager().error(
                    "AutoInfinite",
                    "Ignite nearby TNT to begin",
                    7500L);
            setToggled(false);
            return false;
        }
        final int slot = InventoryUtil.getHotbarSlot(
                (stack) -> stack.getItem() == Items.flint_and_steel
                        || stack.getItem() == Items.fire_charge);
        if (slot == -1)
        {
            Nebula.INSTANCE.getToastManager().error(
                    "AutoInfinite",
                    "Flint & Steel / Fire Charge not found in hotbar",
                    7500L);
            setToggled(false);
            return false;
        }
        Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        final boolean result = InteractionManager.INSTANCE.rightClickBlock(
                blockPos.down(), EnumFacing.UP);
        if (result)
        {
            MC.thePlayer.swingItem();
        }
        Nebula.INSTANCE.getInventoryManager().syncSlot();
        return false;
    }

    private BlockPos handleTNTPlacement(final BlockPos blockPos)
    {
        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos pos = BlockUtil.offset(blockPos, facing);
            if (MC.theWorld.getBlock(pos.getX(), pos.getY(), pos.getZ()) instanceof BlockTNT)
            {
                // we can't open the chest...
                if (facing == EnumFacing.UP)
                {
                    Nebula.INSTANCE.getToastManager().error(
                            "AutoInfinite",
                            "TNT cannot be on top of the chest",
                            7500L);
                    setToggled(false);
                    return null;
                }
                return pos;
            }
        }
        if (!placeTntSetting.getValue())
        {
            Nebula.INSTANCE.getToastManager().error(
                    "AutoInfinite",
                    "Place TNT next to the chest",
                    7500L);
            setToggled(false);
            return null;
        }
        final int tntSlot = InventoryUtil.getHotbarSlot(
                (stack) -> stack.getItem() instanceof ItemBlock
                        && ((ItemBlock) stack.getItem()).getBlock() instanceof BlockTNT);
        if (tntSlot == -1)
        {
            Nebula.INSTANCE.getToastManager().error(
                    "AutoInfinite",
                    "No TNT in your hotbar",
                    7500L);
            setToggled(false);
            return null;
        }
        BlockPos placeBlockPos = null;
        for (final EnumFacing facing : EnumFacing.values())
        {
            if (facing == EnumFacing.UP || facing == EnumFacing.DOWN)
            {
                continue;
            }
            final BlockPos pos = BlockUtil.offset(blockPos, facing);
            if (BlockUtil.isReplaceable(pos))
            {
                placeBlockPos = pos;
                break;
            }
        }
        if (placeBlockPos == null)
        {
            Nebula.INSTANCE.getToastManager().error(
                    "AutoInfinite",
                    "No place found to place TNT",
                    7500L);
            return null;
        }
        Nebula.INSTANCE.getInventoryManager().setSlot(tntSlot);
        final boolean result = InteractionManager.INSTANCE.rightClickBlock(
                placeBlockPos.down(), EnumFacing.UP, true);
        if (result)
        {
            MC.thePlayer.swingItem();
        }
        Nebula.INSTANCE.getInventoryManager().syncSlot();
        return null;
    }

    private boolean isValidItem()
    {
        final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(0);
        if (itemStack == null || itemStack.getItem() == null)
        {
            return false;
        }
        final Item item = itemStack.getItem();
        if (item instanceof ItemBlock)
        {
            final Block block = ((ItemBlock) item).getBlock();
            return block instanceof BlockBed
                    || block instanceof BlockDoor
                    || block instanceof BlockTrapDoor
                    || block instanceof BlockSign
                    || block instanceof BlockLilyPad
                    || block instanceof BlockSlab
                    || block instanceof BlockPotato
                    || block instanceof BlockCarrot
                    || block instanceof BlockCocoa
                    || block instanceof BlockMushroom;
        }
        return item == Items.spawn_egg
                || item == Items.lead
                || item == Items.boat
                || item == Items.dye
                || item == Items.egg
                || item == Items.snowball
                || item == Items.ender_eye
                || item == Items.ender_pearl
                || item == Items.experience_bottle
                || item == Items.fire_charge
                || item == Items.fireworks
                || item == Items.name_tag
                || item == Items.coal
                || item == Items.stick
                || item == Items.string
                || item == Items.arrow
                || item instanceof ItemSkull
                || item instanceof ItemSeeds
                || item instanceof ItemMinecart
                || item instanceof ItemRecord;
    }
}
