package us.nebula.impl.cheat.render;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;

import java.util.List;

/**
 * @author xgraza
 * @since 03/07/25
 * @see net.minecraft.block.Block
 * @see net.minecraft.client.renderer.WorldRenderer
 */
@CheatManifest(name = "XRay",
        description = "Exposes ores or other blocks underground",
        category = CheatCategory.RENDER)
public final class XRayCheat extends Cheat
{
    @CheatInstance
    public static XRayCheat INSTANCE;

    public static final List<Block> XRAY_WHITELIST = Lists.newArrayList(
            Blocks.diamond_ore,
            Blocks.coal_ore,
            Blocks.iron_ore,
            Blocks.emerald_ore,
            Blocks.lapis_ore,
            Blocks.redstone_ore,
            Blocks.lit_redstone_ore,
            Blocks.gold_ore,
            Blocks.quartz_ore,

            Blocks.diamond_block,
            Blocks.coal_block,
            Blocks.iron_block,
            Blocks.emerald_block,
            Blocks.lapis_block,
            Blocks.redstone_block,
            Blocks.gold_block,
            Blocks.quartz_block,

            Blocks.beacon,
            Blocks.anvil,
            Blocks.ender_chest,
            Blocks.chest,
            Blocks.trapped_chest,
            Blocks.furnace,
            Blocks.lit_furnace,
            Blocks.enchanting_table,
            Blocks.dragon_egg,
            Blocks.monster_egg,
            Blocks.mob_spawner,
            Blocks.bed,
            Blocks.bookshelf,
            Blocks.cauldron,
            Blocks.brewing_stand,
            Blocks.sponge,

            Blocks.water,
            Blocks.flowing_water,
            Blocks.lava,
            Blocks.flowing_lava
    );

    private final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.BASIC)
            .onValueChange((o, v) ->
            {
                if (!isToggled())
                {
                    return;
                }
                if (MC.theWorld != null)
                {
                    MC.renderGlobal.loadRenderers();
                }
            });

    @Override
    protected void onEnable()
    {
        super.onEnable();
        if (MC.theWorld == null)
        {
            return;
        }
        MC.renderGlobal.loadRenderers();
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        if (MC.theWorld == null)
        {
            return;
        }
        MC.renderGlobal.loadRenderers();
    }

    public boolean isWireframe()
    {
        return modeSetting.getValue() == Mode.WIREFRAME;
    }

    public boolean isTransparent()
    {
        return modeSetting.getValue() == Mode.TRANSPARENT;
    }

    private enum Mode
    {
        BASIC, WIREFRAME, TRANSPARENT
    }
}
