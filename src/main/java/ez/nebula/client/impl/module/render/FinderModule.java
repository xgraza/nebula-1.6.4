package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.render.EventRender3D;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.world.BlockSearcher;
import ez.nebula.client.util.render.QuadMask;
import ez.nebula.client.util.render.RenderUtil;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author xgraza
 * @since 6/4/26
 */
@DebugFeature
@ModuleManifest(name = "Finder",
        description = "Highlights selected blocks",
        category = ModuleCategory.RENDER)
public final class FinderModule extends Module
{
    private static final List<Block> BLOCK_LIST = new ArrayList<>();

    private final Set<BlockPos> posSet = new ConcurrentSet<>();

    private final BlockSearcher searcher = new BlockSearcher("Finder", (block) ->
    {
        if (BLOCK_LIST.contains(block.getBlock()))
        {
            posSet.add(new BlockPos(block.getX(), block.getY(), block.getZ()));
        }
    });

    @Override
    public void onEnable()
    {
        super.onEnable();
        searcher.setSearchRange(5);
        searcher.setSearching(true);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        searcher.setSearching(false);
        posSet.clear();
    }

    @Subscribe
    private final EventListener<EventRender3D> updateEventListener = event ->
    {
        if (!searcher.isSearching())
        {
            return;
        }
        for (final BlockPos pos : posSet)
        {
            RenderUtil.renderFilledAABB(new AxisAlignedBB(pos), QuadMask.ALL_FACES, 0x30FFFFFF);
        }
    };
}
