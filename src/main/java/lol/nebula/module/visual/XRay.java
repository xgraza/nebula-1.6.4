package lol.nebula.module.visual;

import com.google.common.collect.Lists;
import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.render.world.EventRenderBlockSide;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.module.visual.xray.XrayBlocksConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author aesthetical
 * @since 05/16/23
 */
public class XRay extends Module {
    public static final Set<Integer> BLOCK_LIST = new HashSet<>();

    public XRay() {
        super("X-Ray", "Lets you see ores through blocks", ModuleCategory.VISUAL);
        List<Block> defaults = Lists.newArrayList(
                Blocks.diamond_ore, Blocks.diamond_block,
                Blocks.gold_ore, Blocks.gold_block,
                Blocks.iron_ore, Blocks.iron_block,
                Blocks.lapis_ore, Blocks.lapis_block,
                Blocks.coal_ore, Blocks.coal_block,
                Blocks.emerald_ore, Blocks.emerald_block,
                Blocks.quartz_ore, Blocks.quartz_ore,
                Blocks.crafting_table, Blocks.furnace, Blocks.lit_furnace,
                Blocks.beacon, Blocks.enchanting_table, Blocks.anvil);
        defaults.forEach((x) -> BLOCK_LIST.add(Block.getIdFromBlock(x)));

        new XrayBlocksConfig();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (mc.renderGlobal != null && mc.theWorld != null)
            mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (mc.renderGlobal != null && mc.theWorld != null)
            mc.renderGlobal.loadRenderers();
    }

    @Listener
    public void onRenderBlockSide(EventRenderBlockSide event) {
        event.setRenderSide(BLOCK_LIST.contains(Block.getIdFromBlock(event.getBlock())));
        event.cancel();
    }
}
