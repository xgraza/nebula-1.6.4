package us.nebula.client.cheat.impl.world;

import net.minecraft.block.BlockTorch;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.ItemBlock;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import us.nebula.client.Nebula;
import us.nebula.client.cheat.impl.combat.AutoBedCheat;
import us.nebula.client.cheat.impl.combat.KillAuraCheat;
import us.nebula.client.interaction.InteractionManager;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.setting.Setting;
import us.nebula.client.util.math.MathUtil;
import us.nebula.client.util.player.InventoryUtil;
import us.nebula.client.util.player.PlayerUtil;
import us.nebula.client.util.world.BlockUtil;

/**
 * @author xgraza
 * @since 06/22/25
 */
@CheatManifest(name = "AutoTorch",
        description = "Automatically places torches to prevent mob spawns",
        category = CheatCategory.WORLD)
public final class AutoTorchCheat extends Cheat
{
    @CheatInstance
    public static AutoTorchCheat INSTANCE;

    private final Setting<Double> rangeSetting = numberBuilder("Range", 4.5)
            .setMin(1.0)
            .setMax(6.0)
            .setScale(0.1)
            .setDescription("How far to place torches")
            .build();
    private final Setting<Integer> minLightLevelSetting = numberBuilder("Min Light Level", 7)
            .setMin(0)
            .setMax(12)
            .setScale(1)
            .setDescription("The minimum light level needed to place a torch")
            .build();
    private final Setting<Boolean> spawnCheckSetting = builder("Spawn Check", true)
            .setDescription("If to check if a mob can spawn on a block to place a torch")
            .build();

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (MC.thePlayer != null)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        // do not interfere with KillAura or AutoBed
        // if we try to place with killaura, it'll delay our attacks and possibly get us killed
        if (KillAuraCheat.INSTANCE.isActive() || AutoBedCheat.INSTANCE.isActive())
        {
            return;
        }

        final int slot = InventoryUtil.getHotbarSlot((stack) ->
                stack.getItem() instanceof ItemBlock
                        && ((ItemBlock) stack.getItem()).getBlock() instanceof BlockTorch);
        if (slot == -1)
        {
            return;
        }
        final BlockPos pos = getPlacePos();
        if (pos == null)
        {
            return;
        }
        Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        InteractionManager.INSTANCE.rightClickBlock(pos.down(), EnumFacing.UP, true);
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    };

    private BlockPos getPlacePos()
    {
        final BlockPos origin = PlayerUtil.getOrigin();
        final int r = rangeSetting.getValue().intValue();

        BlockPos placeBlockPos = null;
        double distance = 0.0;

        for (int x = -r; x <= r; ++x)
        {
            for (int z = -r; z <= r; ++z)
            {
                final BlockPos pos = origin.add(x, 0, z);
                if (!BlockUtil.isReplaceable(pos) || MC.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ()) > rangeSetting.getValue())
                {
                    continue;
                }
                final BiomeGenBase base = MC.theWorld.getBiomeGenForCoords(pos.getX() & 15, pos.getZ() & 15);
                if (spawnCheckSetting.getValue()
                        && (base.getSpawningChance() <= 0.0f
                        || base.getSpawnableList(EnumCreatureType.monster).isEmpty()))
                {
                    continue;
                }
                final Chunk chunk = MC.theWorld.getChunkFromBlockCoords(pos.getX(), pos.getZ());
                if (spawnCheckSetting.getValue() &&
                        !SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.monster,
                                MC.theWorld, pos.getX(), pos.getY(), pos.getZ()))
                {
                    continue;
                }
                final int blockLighting = chunk.getSavedLightValue(EnumSkyBlock.Block,
                        pos.getX() & 15, pos.getY(), pos.getZ() & 15);
                if (blockLighting > minLightLevelSetting.getValue())
                {
                    continue;
                }
                final double distToVec = MathUtil.getDistance(origin, pos);
                if (placeBlockPos == null || distToVec < distance)
                {
                    placeBlockPos = pos;
                    distance = distToVec;
                }
            }
        }

        return placeBlockPos;
    }
}
