package us.nebula.client.impl.cheat.movement;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import us.nebula.client.Nebula;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.impl.event.render.EventRender3D;
import us.nebula.client.util.player.ChatUtil;
import us.nebula.client.util.player.PlayerUtil;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.opengl.GL11.*;

@CheatManifest(name = "PathFinder", category = CheatCategory.MOVEMENT)
public final class PathFinderCheat extends Cheat
{
    private final Queue<BlockPos> pathQueue = new LinkedBlockingQueue<>();
    private BlockPos currentPosition, nextPosition;

    @Override
    public void onEnable()
    {
        super.onEnable();
        if (MC.theWorld == null || MC.thePlayer == null)
        {
            setToggled(false);
            return;
        }
        pathQueue.clear();
        currentPosition = null;

        final BlockPos originPos = PlayerUtil.getOrigin();
        final BlockPos goalPos = new BlockPos(384, 115, -1291);
        ChatUtil.send("Pathing to %s", goalPos);
        final List<BlockPos> pathList = Nebula.INSTANCE.getMovementController().getPathTo(originPos, goalPos);
        if (pathList.isEmpty())
        {
            ChatUtil.send("Empty path list");
            setToggled(false);
            return;
        }

        pathQueue.addAll(pathList);
        Nebula.INSTANCE.getMovementController().override();
        ChatUtil.send("%s nodes discovered for path to %s", pathQueue.size(), goalPos);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (MC.thePlayer != null)
        {
            Nebula.INSTANCE.getMovementController().restore();
        }
    }

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glDisable(GL_TEXTURE_2D);

        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);

        glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

        glBegin(GL_LINE_STRIP);

        glVertex3d(MC.thePlayer.posX, MC.thePlayer.boundingBox.minY + 0.2, MC.thePlayer.posZ);

        if (currentPosition != null)
        {
            glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
            glVertex3d(currentPosition.getX() + 0.5, currentPosition.getY() + 0.2, currentPosition.getZ() + 0.5);
        }

        if (nextPosition != null)
        {
            glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            glVertex3d(nextPosition.getX() + 0.5, nextPosition.getY() + 0.2, nextPosition.getZ() + 0.5);
        }

        glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        for (final BlockPos pathPos : pathQueue)
        {
            glVertex3d(pathPos.getX() + 0.5, pathPos.getY() + 0.2, pathPos.getZ() + 0.5);
        }
        glEnd();

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_BLEND);
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (currentPosition == null)
        {
            currentPosition = pathQueue.poll();
            if (pathQueue.isEmpty())
            {
                ChatUtil.send("Done!");
                Nebula.INSTANCE.getMovementController().setMovement(0.0f, 0.0f);
                setToggled(false);
                return;
            }
            nextPosition = pathQueue.poll();
            return;
        }
        final float[] movement = Nebula.INSTANCE.getMovementController().getMovementFor(
                Vec3.createVectorHelper(
                        currentPosition.getX() + 0.5,
                        currentPosition.getY(),
                        currentPosition.getZ() + 0.5));
        Nebula.INSTANCE.getMovementController().setMovement(movement);

        if (Nebula.INSTANCE.getMovementController().isJumping() && MC.thePlayer.onGround)
        {
            Nebula.INSTANCE.getMovementController().jump(false);
        }

        if (currentPosition.getY() - MC.thePlayer.boundingBox.minY >= 1)
        {
            Nebula.INSTANCE.getMovementController().jump(true);
        }

        final AxisAlignedBB bb = new AxisAlignedBB(currentPosition);
        if (MC.thePlayer.boundingBox.copy().intersectsWith(bb))
        {
            if (nextPosition != null && nextPosition.getY() - MC.thePlayer.boundingBox.minY >= 1)
            {
                Nebula.INSTANCE.getMovementController().jump(true);
            }

            currentPosition = nextPosition;
            nextPosition = pathQueue.poll();
        }
    };
}
