package us.nebula.client.cheat.impl.render;

import net.minecraft.util.AxisAlignedBB;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.setting.Setting;
import us.nebula.client.util.render.QuadMask;
import us.nebula.client.listener.event.render.EventRender3D;
import us.nebula.client.util.render.RenderUtil;

/**
 * @author xgraza
 * @since 03/07/25
 */
@CheatManifest(name = "ChunkBoundaries",
        description = "Renders a chunk boundary in the chunk you're currently standing in",
        category = CheatCategory.RENDER)
public final class ChunkBoundariesCheat extends Cheat
{
    private final Setting<Mode> modeSetting = enumBuilder("Mode", Mode.BORDERS)
            .setDescription("How to render a chunk boundary")
            .build();
    private final Setting<Float> lineWidthSetting = numberBuilder("Line Width", 1.5f)
            .setMin(0.5f)
            .setMax(5.0f)
            .setScale(0.1f)
            .setDescription("What width the boundary should be rendered with")
            .build();

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        final int chunkBlockCoordX = MC.thePlayer.chunkCoordX * 16;
        final int chunkBlockCoordZ = MC.thePlayer.chunkCoordZ * 16;

        if (modeSetting.getValue() == Mode.BORDERS)
        {
            RenderUtil.renderOutlinedAABB(AxisAlignedBB.getBoundingBox(
                    chunkBlockCoordX, 0.0, chunkBlockCoordZ,
                    chunkBlockCoordX + 16.0, 256.0, chunkBlockCoordZ + 16.0
            ), lineWidthSetting.getValue(), QuadMask.ALL_FACES, 0xFFFF0000);
        } else
        {
            // getBlockStorageArray() essentially but no data pulled
            for (int i = 0; i < 16; ++i)
            {
                RenderUtil.renderOutlinedAABB(AxisAlignedBB.getBoundingBox(
                        chunkBlockCoordX, i * 16.0, chunkBlockCoordZ,
                        chunkBlockCoordX + 16.0, (i + 1) * 16.0, chunkBlockCoordZ + 16.0
                ), lineWidthSetting.getValue(), QuadMask.ALL_FACES, 0xFFFF0000);
            }
        }
    };

    private enum Mode
    {
        BORDERS, SECTION
    }
}
