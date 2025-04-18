package us.nebula.impl.cheat.miscellaneous;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.src.BlockPos;
import us.nebula.api.DebugFeature;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.pathfinding.PathProcessor;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.render.EventRender3D;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@DebugFeature
@CheatManifest(name = "Pathfinder", category = CheatCategory.MISCELLANEOUS)
public final class PathfinderCheat extends Cheat
{
    private PathProcessor pathfinder;
    private boolean attempted;

    private int list;

    public PathfinderCheat()
    {
        pathfinder = new PathProcessor();
        //moveProcessor = new MoveProcessor(pathfinder);
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        attempted = false;
        pathfinder.reset();
    }

    @Subscribe
    private final EventListener<EventRender3D> render2DEventListener = event ->
    {
        if (!pathfinder.isProcessed())
        {
            return;
        }

        final List<BlockPos> path = pathfinder.getPath();
        if (path.isEmpty())
        {
            return;
        }

        drawPath(path, 0.0f, 0.0f, 1.0f);
    };

    private void drawPath(final List<BlockPos> path, float r, float g, float b)
    {
        if (path.isEmpty())
        {
            return;
        }
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(770, 771);

        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(2.5f);

        glColor4f(r, g, b, 0.85f);

        glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

        glBegin(GL_LINE_STRIP);
        {
            for (final BlockPos pos : path)
            {
                glVertex3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            }
        }
        glEnd();

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glLineWidth(1.0f);

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (!attempted)
        {
            attempted = true;
            pathfinder.process(new BlockPos(0, 4, 0));
        }
        //moveProcessor.updateMovement();
    };
}
