package ez.nebula.client.impl.module.world;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.item.ItemBlock;

/**
 * @author xgraza
 * @since 6/2/26
 */
@ModuleManifest(name = "AutoTree",
        description = "Automatically plants sapplings and bonemeals them",
        category = ModuleCategory.WORLD)
public final class AutoTreeModule extends Module
{
    private final Setting<Double> rangeSetting = numberBuilder("Range", 4.5)
            .setMin(1.0)
            .setMax(6.0)
            .setScale(0.5)
            .setDescription("How far to plant or bonemeal saplings")
            .build();

    private final Setting<Boolean> plantSetting = builder("Plant", true)
            .setDescription("If to automatically plant saplings")
            .build();
    private final Setting<Boolean> oakSaplingSetting = builder("Oak Saplings", true)
            .setDescription("If to plant oak saplings")
            .setVisibility((value) -> plantSetting.getValue())
            .build();
    private final Setting<Boolean> darkOakSaplingSetting = builder("Dark Oak Saplings", true)
            .setDescription("If to plant dark oak saplings")
            .setVisibility((value) -> plantSetting.getValue())
            .build();
    private final Setting<Boolean> birchSaplingSetting = builder("Birch Saplings", true)
            .setDescription("If to plant birch saplings")
            .setVisibility((value) -> plantSetting.getValue())
            .build();
    private final Setting<Boolean> spruceSaplingSetting = builder("Spruce Saplings", true)
            .setDescription("If to plant spruce saplings")
            .setVisibility((value) -> plantSetting.getValue())
            .build();
    private final Setting<Boolean> acaciaSaplingSetting = builder("Acacia Saplings", true)
            .setDescription("If to plant acacia saplings")
            .setVisibility((value) -> plantSetting.getValue())
            .build();
    private final Setting<Boolean> jungleSaplingSetting = builder("Jungle Saplings", true)
            .setDescription("If to plant jungle saplings")
            .setVisibility((value) -> plantSetting.getValue())
            .build();

    private final Setting<Boolean> bonemealSetting = builder("Bonemeal", false)
            .setDescription("If to automatically bonemeal saplings")
            .build();
    private final Setting<Integer> packetsSetting = numberBuilder("Packets", 5)
            .setMin(1)
            .setMax(20)
            .setScale(1)
            .setDescription("How many times to bonemeal a sapling in a tick")
            .setVisibility((value) -> bonemealSetting.getValue())
            .build();

    private int getSapplingSlot()
    {
        return InventoryUtil.getSlot(0, 9, (stack) ->
        {
            if (!(stack.getItem() instanceof ItemBlock))
            {
                return false;
            }
            final Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (!(block instanceof BlockSapling))
            {
                return false;
            }
            final BlockSapling sapling = (BlockSapling) block;
            //stack.getItemDamage();
            return false;
        });
    }
}
