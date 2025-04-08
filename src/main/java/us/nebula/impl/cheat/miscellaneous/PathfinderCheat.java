package us.nebula.impl.cheat.miscellaneous;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import us.nebula.api.DebugFeature;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.pathfinding.MoveProcessor;
import us.nebula.api.pathfinding.Node;
import us.nebula.api.pathfinding.Pathfinder;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.render.EventRender3D;
import us.nebula.util.player.ChatUtil;
import us.nebula.util.render.RenderUtil;

import java.util.List;
import java.util.Queue;

import static org.lwjgl.opengl.GL11.*;

@DebugFeature
@CheatManifest(name = "Pathfinder", category = CheatCategory.MISCELLANEOUS)
public final class PathfinderCheat extends Cheat
{
    private Pathfinder pathfinder;
    private MoveProcessor moveProcessor;
    private boolean attempted;

    public PathfinderCheat()
    {
        pathfinder = new Pathfinder();
        moveProcessor = new MoveProcessor(pathfinder);
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        attempted = false;
        moveProcessor.resetMovement();
    }

    @Subscribe
    private final EventListener<EventRender3D> render2DEventListener = event ->
    {
        final List<Node> nodeList = pathfinder.getNodes();
        if (nodeList.isEmpty())
        {
            return;
        }

        if (moveProcessor.getGoalBlockPos() != null)
        {
            final BlockPos b = moveProcessor.getGoalBlockPos();
            RenderUtil.filledBox3D(new AxisAlignedBB(b), 0, 0xAAFF0000);
        }

        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);
        glBlendFunc(770, 771);

        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(2.5f);

        glColor4f(1.0f, 0.2f, 0.2f, 0.85f);

        glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

        glBegin(GL_LINE_STRIP);
        {
            for (final Node node : nodeList)
            {
                final BlockPos pos = node.getPos();
                glVertex3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            }
        }
        glEnd();

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glLineWidth(1.0f);

        glEnable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_BLEND);
        glPopMatrix();
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (!attempted)
        {
            attempted = true;
            pathfinder.pathfind(new BlockPos(0, 4, 0));
        }
        moveProcessor.updateMovement();
    };
}
