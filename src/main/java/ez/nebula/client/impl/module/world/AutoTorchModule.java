package ez.nebula.client.impl.module.world;

import ez.nebula.client.api.listener.event.render.EventRender2D;
import ez.nebula.client.api.listener.event.render.EventRender3D;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.impl.module.combat.KillAuraModule;
import ez.nebula.client.util.math.AngleUtil;
import net.minecraft.block.BlockTorch;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.ItemBlock;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.impl.module.combat.AutoBedModule;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.math.MathUtil;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.minecraft.world.BlockUtil;

/**
 * @author xgraza
 * @since 06/22/25
 */
@ModuleManifest(name = "AutoTorch",
        description = "Automatically places torches to prevent mob spawns",
        category = ModuleCategory.WORLD)
public final class AutoTorchModule extends Module
{
    @ModuleInstance
    public static AutoTorchModule INSTANCE;

    private static final int AUTO_TORCH_ROTATION_PRIORITY = 10;

    private final NumberSetting<Double> rangeSetting = numberBuilder("Range", 4.5)
            .setMin(1.0)
            .setMax(6.0)
            .setScale(0.1)
            .setDescription("How far to place torches")
            .build();
    private final NumberSetting<Integer> minLightLevelSetting = numberBuilder("Min Light Level", 7)
            .setMin(0)
            .setMax(12)
            .setScale(1)
            .setDescription("The minimum light level needed to place a torch")
            .build();
    private final Setting<Boolean> spawnCheckSetting = builder("Spawn Check", true)
            .setDescription("If to check if a mob can spawn on a block to place a torch")
            .build();
    private final Setting<Boolean> rotateSetting = builder("Rotate", false)
            .setDescription("If to rotate when placing a torch")
            .build();

    private BlockPos pos;
    private float[] angles;

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (MC.thePlayer != null)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
        angles = null;
        pos = null;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        // do not interfere with KillAura or AutoBed
        // if we try to place with killaura, it'll delay our attacks and possibly get us killed
        if (KillAuraModule.INSTANCE.getTarget() != null || AutoBedModule.INSTANCE.isActive())
        {
            return;
        }

        final int slot = InventoryUtil.getHotbarSlot((stack) ->
                stack.getItem() instanceof ItemBlock
                        && ((ItemBlock) stack.getItem()).getBlock() instanceof BlockTorch);
        if (slot == InventoryUtil.INVALID_SLOT)
        {
            return;
        }
        pos = getPlacePos();
        if (pos == null)
        {
            return;
        }

        if (rotateSetting.getValue())
        {
            if (angles == null)
            {
                return;
            }
            if (!Nebula.INSTANCE.getRotationManager().spoof(angles[0], angles[1], AUTO_TORCH_ROTATION_PRIORITY))
            {
                return;
            }
        }

        Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        InteractionManager.INSTANCE.rightClickBlock(pos.down(), EnumFacing.UP, true);
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    };

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (pos != null)
        {
            angles = AngleUtil.anglesToBlock(pos, EnumFacing.UP, event.getPartialTicks());
        }
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
                final BlockPos under = pos.down();
                if (!BlockUtil.isReplaceable(pos)
                        || BlockUtil.isReplaceable(under)
                        || MC.thePlayer.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > rangeSetting.getValue())
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
