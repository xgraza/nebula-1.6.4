package us.nebula.client.impl.cheat.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
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
import us.nebula.client.util.player.PlayerUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xgraza
 * @since 06/24/25
 */
// TODO: sugar cane & nether warts
@CheatManifest(name = "AutoFarm",
        description = "Automatically harvests and replants seeds",
        category = CheatCategory.WORLD)
public final class AutoFarmCheat extends Cheat
{
    private static final Map<Block, Item> CROP_BLOCK_TO_SEED = new HashMap<>();

    static
    {
        CROP_BLOCK_TO_SEED.put(Blocks.wheat, Items.wheat_seeds);
        CROP_BLOCK_TO_SEED.put(Blocks.melon_stem, Items.melon_seeds);
        CROP_BLOCK_TO_SEED.put(Blocks.pumpkin_stem, Items.pumpkin_seeds);
        CROP_BLOCK_TO_SEED.put(Blocks.potatoes, Items.potato);
        CROP_BLOCK_TO_SEED.put(Blocks.carrots, Items.carrot);
    }

    private final Setting<Double> rangeSetting = new Setting<>(
            "Range", 4.5, 1.0, 6.0, 0.5);
    private final Setting<Boolean> noPosionousSetting = new Setting<>(
            "Throw Out Poisonous", true);

    private final Map<BlockPos, Block> plantTypeAtBlockMap = new ConcurrentHashMap<>();

    private int oldSlot = -1;
    private BlockPos melonBreakPos;

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (oldSlot != -1 && MC.thePlayer != null)
        {
            MC.thePlayer.inventory.currentItem = oldSlot;
        }

        oldSlot = -1;
        melonBreakPos = null;
        plantTypeAtBlockMap.clear();
        PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (melonBreakPos != null)
        {
            if (InteractionManager.INSTANCE.breakBlock(melonBreakPos, EnumFacing.UP))
            {
                MC.thePlayer.inventory.currentItem = oldSlot;
                oldSlot = -1;
                return;
            }
            melonBreakPos = null;
            oldSlot = -1;
        }

        cacheSurroundingFarmland();

        if (noPosionousSetting.getValue())
        {
            throwOutPoisonous();
        }

        for (final BlockPos pos : plantTypeAtBlockMap.keySet())
        {
            // keep in cache, but too far away to harvest / check...
            if (MC.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ()) > rangeSetting.getValue())
            {
                continue;
            }

            if (!(MC.theWorld.getBlock(pos) instanceof BlockFarmland))
            {
                plantTypeAtBlockMap.remove(pos);
                continue;
            }
            final Block cropBlockType = plantTypeAtBlockMap.get(pos);
            if (cropBlockType == null)
            {
                continue;
            }

            final BlockPos cropBlockPos = pos.up();
            final Block block = MC.theWorld.getBlock(cropBlockPos);
            if (block == Blocks.air)
            {
                plantSeed(pos, cropBlockType);
                return;
            }

            final int meta = MC.theWorld.getBlockMetadata(cropBlockPos.getX(),
                    cropBlockPos.getY(), cropBlockPos.getZ());
            if (meta < 7)
            {
                continue;
            }

            if (InteractionManager.INSTANCE.breakBlock(cropBlockPos, EnumFacing.UP))
            {
                plantSeed(pos, cropBlockType);
                if (cropBlockType != Blocks.melon_stem && cropBlockType != Blocks.pumpkin_stem)
                {
                    return;
                }
            }

            if (cropBlockType == Blocks.melon_stem || cropBlockType == Blocks.pumpkin_stem)
            {
                final BlockPos melonPos = findGrownMelonCrop(cropBlockPos);
                if (melonPos == null)
                {
                    return;
                }
                // melon & pumpkins both can be harvested with the same type of tool, just search for the best
                final int slot = InventoryUtil.getBestToolSlotFor(Blocks.pumpkin);
                if (slot != -1)
                {
                    oldSlot = MC.thePlayer.inventory.currentItem;
                    MC.thePlayer.inventory.currentItem = slot;
                }
                // if we can instant remove it, dont bother bringing to the next tick...
                if (InteractionManager.INSTANCE.breakBlock(melonPos, EnumFacing.UP))
                {
                    MC.thePlayer.inventory.currentItem = oldSlot;
                    oldSlot = -1;
                    return;
                }
                melonBreakPos = melonPos;
                return;
            }
        }

        PlayerControllerMP.ALLOW_BREAK_OVERRIDE = false;
    };

    private BlockPos findGrownMelonCrop(final BlockPos pos)
    {
        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos n = pos.offset(facing);
            final Block block = MC.theWorld.getBlock(n);
            if (block == Blocks.melon_block || block == Blocks.pumpkin)
            {
                return n;
            }
        }
        return null;
    }

    private void throwOutPoisonous()
    {
        final int slot = InventoryUtil.getSlot(0, 36,
                (stack) -> stack.getItem() == Items.poisonous_potato);
        if (slot != -1)
        {
            MC.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 1, 4, MC.thePlayer);
        }
    }

    private void plantSeed(final BlockPos pos, final Block type)
    {
        final int slot = InventoryUtil.getHotbarSlot(
                (stack) -> stack.getItem() == CROP_BLOCK_TO_SEED.get(type));
        if (slot == -1)
        {
            return;
        }
        Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        InteractionManager.INSTANCE.rightClickBlock(pos, EnumFacing.UP);
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    }

    private void cacheSurroundingFarmland()
    {
        final BlockPos origin = PlayerUtil.getOrigin();
        final int r = rangeSetting.getValue().intValue();
        for (int y = -1; y < 2; ++y)
        {
            for (int x = -r; x <= r; ++x)
            {
                for (int z = -r; z <= r; ++z)
                {
                    final BlockPos pos = origin.add(x, y, z);
                    if (!plantTypeAtBlockMap.containsKey(pos)
                            && MC.theWorld.getBlock(pos) instanceof BlockFarmland)
                    {
                        final Block cropBlock = MC.theWorld.getBlock(pos.up());
                        if (cropBlock == Blocks.wheat
                                || cropBlock == Blocks.melon_stem
                                || cropBlock == Blocks.pumpkin_stem
                                || cropBlock == Blocks.potatoes
                                || cropBlock == Blocks.carrots)
                        {
                            plantTypeAtBlockMap.put(pos, cropBlock);
                        }
                    }
                }
            }
        }
    }
}
