package us.nebula.impl.cheat.render;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.render.EventRender3D;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 03/07/25
 */
@CheatManifest(name = "ChunkBoundaries",
        description = "Renders a chunk boundary",
        category = CheatCategory.RENDER)
public final class ChunkBoundariesCheat extends Cheat
{
    private final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.BORDERS);

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        final int chunkBlockCoordX = MC.thePlayer.chunkCoordX * 16;
        final int chunkBlockCoordZ = MC.thePlayer.chunkCoordZ * 16;

        glPushMatrix();

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
        glLineWidth(2.5f);

        if (modeSetting.getValue() == Mode.BORDERS)
        {
            RenderGlobal.drawOutlinedBoundingBox(AxisAlignedBB.getBoundingBox(
                    chunkBlockCoordX, 0.0, chunkBlockCoordZ,
                    chunkBlockCoordX + 16.0, 256.0, chunkBlockCoordZ + 16.0
            ), 0xFFFF0000);
        } else
        {
            // getBlockStorageArray() essentially but no data pulled
            for (int i = 0; i < 16; ++i)
            {
                RenderGlobal.drawOutlinedBoundingBox(AxisAlignedBB.getBoundingBox(
                        chunkBlockCoordX, i * 16.0, chunkBlockCoordZ,
                        chunkBlockCoordX + 16.0, (i + 1) * 16.0, chunkBlockCoordZ + 16.0
                ), 0xFFFF0000);
            }
        }

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);

        glPopMatrix();
    };

    private enum Mode
    {
        BORDERS, SECTION
    }
}
