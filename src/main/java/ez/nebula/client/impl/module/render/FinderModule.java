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
import net.minecraft.init.Blocks;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;

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
    private final BlockSearcher searcher = new BlockSearcher();

    @Override
    public void onEnable()
    {
        super.onEnable();
        searcher.setSearchRange(5);
        searcher.setSearching(true);
        searcher.addSearchBlocks(Blocks.portal);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        searcher.setSearching(false);
    }

    @Subscribe
    private final EventListener<EventRender3D> updateEventListener = event ->
    {
        if (!searcher.isSearching())
        {
            return;
        }
        final Set<BlockPos> foundBlockList = searcher.getFoundPositionList();
        for (final BlockPos pos : foundBlockList)
        {
            RenderUtil.renderFilledAABB(new AxisAlignedBB(pos), QuadMask.ALL_FACES, 0x30FFFFFF);
        }
    };
}
