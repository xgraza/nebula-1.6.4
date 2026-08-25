package ez.nebula.client.util.render.world;

import ez.nebula.client.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 8/24/26
 */
public final class Render3D
{
    private static final Minecraft MC = Minecraft.getMinecraft();
    private static final Tessellator TESSELLATOR = Tessellator.instance;

    /**
     * Draws a filled {@link AxisAlignedBB}
     * @param box the {@link AxisAlignedBB}
     * @param renderMask the face mask
     * @param color the color of the filled bounding box to render
     */
    public static void filledAABB(AxisAlignedBB box, final int renderMask, final int color)
    {
        glPushMatrix();

        glDisable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);

        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);

        glDisable(GL_CULL_FACE);

        box = box.copy().offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

        if (QuadMask.has(renderMask, QuadMask.UP))
        {
            TESSELLATOR.startDrawingQuads();
            RenderUtil.setTessellatorColor(color);
            TESSELLATOR.addVertex(box.minX, box.minY, box.minZ);
            TESSELLATOR.addVertex(box.maxX, box.minY, box.minZ);
            TESSELLATOR.addVertex(box.maxX, box.minY, box.maxZ);
            TESSELLATOR.addVertex(box.minX, box.minY, box.maxZ);
            TESSELLATOR.draw();
        }

        if (QuadMask.has(renderMask, QuadMask.SOUTH))
        {
            TESSELLATOR.startDrawingQuads();
            RenderUtil.setTessellatorColor(color);
            TESSELLATOR.addVertex(box.minX, box.minY, box.maxZ);
            TESSELLATOR.addVertex(box.maxX, box.minY, box.maxZ);
            TESSELLATOR.addVertex(box.maxX, box.maxY, box.maxZ);
            TESSELLATOR.addVertex(box.minX, box.maxY, box.maxZ);
            TESSELLATOR.draw();
        }

        if (QuadMask.has(renderMask, QuadMask.NORTH))
        {
            TESSELLATOR.startDrawingQuads();
            RenderUtil.setTessellatorColor(color);
            TESSELLATOR.addVertex(box.maxX, box.minY, box.minZ);
            TESSELLATOR.addVertex(box.minX, box.minY, box.minZ);
            TESSELLATOR.addVertex(box.minX, box.maxY, box.minZ);
            TESSELLATOR.addVertex(box.maxX, box.maxY, box.minZ);
            TESSELLATOR.draw();
        }

        if (QuadMask.has(renderMask, QuadMask.WEST))
        {
            TESSELLATOR.startDrawingQuads();
            RenderUtil.setTessellatorColor(color);
            TESSELLATOR.addVertex(box.maxX, box.minY, box.maxZ);
            TESSELLATOR.addVertex(box.maxX, box.minY, box.minZ);
            TESSELLATOR.addVertex(box.maxX, box.maxY, box.minZ);
            TESSELLATOR.addVertex(box.maxX, box.maxY, box.maxZ);
            TESSELLATOR.draw();
        }

        if (QuadMask.has(renderMask, QuadMask.EAST))
        {
            TESSELLATOR.startDrawingQuads();
            RenderUtil.setTessellatorColor(color);
            TESSELLATOR.addVertex(box.minX, box.minY, box.minZ);
            TESSELLATOR.addVertex(box.minX, box.minY, box.maxZ);
            TESSELLATOR.addVertex(box.minX, box.maxY, box.maxZ);
            TESSELLATOR.addVertex(box.minX, box.maxY, box.minZ);
            TESSELLATOR.draw();
        }

        if (QuadMask.has(renderMask, QuadMask.DOWN))
        {
            // top
            TESSELLATOR.startDrawingQuads();
            RenderUtil.setTessellatorColor(color);
            TESSELLATOR.addVertex(box.minX, box.maxY, box.maxZ);
            TESSELLATOR.addVertex(box.maxX, box.maxY, box.maxZ);
            TESSELLATOR.addVertex(box.maxX, box.maxY, box.minZ);
            TESSELLATOR.addVertex(box.minX, box.maxY, box.minZ);
            TESSELLATOR.draw();
        }

        glEnable(GL_CULL_FACE);

        glEnable(GL_TEXTURE_2D);

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);

        glPopMatrix();
    }

    /**
     * Draws an outlined {@link AxisAlignedBB}
     * @param box the {@link AxisAlignedBB}
     * @param lineWidth the width of the outlined bounding box
     * @param renderMask the face mask
     * @param color the color of the outline
     */
    public static void outlinedAABB(AxisAlignedBB box, final float lineWidth, final int renderMask, final int color)
    {
        glPushMatrix();

        glDisable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);

        glLineWidth(lineWidth);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);

        glDisable(GL_CULL_FACE);

        box = box.copy().offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

        if (QuadMask.has(renderMask, QuadMask.UP))
        {
            TESSELLATOR.startDrawing(GL_LINE_LOOP);
            RenderUtil.setTessellatorColor(color);
            TESSELLATOR.addVertex(box.minX, box.minY, box.minZ);
            TESSELLATOR.addVertex(box.maxX, box.minY, box.minZ);
            TESSELLATOR.addVertex(box.maxX, box.minY, box.maxZ);
            TESSELLATOR.addVertex(box.minX, box.minY, box.maxZ);
            TESSELLATOR.draw();
        }

        if (QuadMask.has(renderMask, QuadMask.SOUTH))
        {
            TESSELLATOR.startDrawing(GL_LINE_LOOP);
            RenderUtil.setTessellatorColor(color);
            TESSELLATOR.addVertex(box.minX, box.minY, box.maxZ);
            TESSELLATOR.addVertex(box.maxX, box.minY, box.maxZ);
            TESSELLATOR.addVertex(box.maxX, box.maxY, box.maxZ);
            TESSELLATOR.addVertex(box.minX, box.maxY, box.maxZ);
            TESSELLATOR.draw();
        }

        if (QuadMask.has(renderMask, QuadMask.NORTH))
        {
            TESSELLATOR.startDrawing(GL_LINE_LOOP);
            RenderUtil.setTessellatorColor(color);
            TESSELLATOR.addVertex(box.maxX, box.minY, box.minZ);
            TESSELLATOR.addVertex(box.minX, box.minY, box.minZ);
            TESSELLATOR.addVertex(box.minX, box.maxY, box.minZ);
            TESSELLATOR.addVertex(box.maxX, box.maxY, box.minZ);
            TESSELLATOR.draw();
        }

        if (QuadMask.has(renderMask, QuadMask.WEST))
        {
            TESSELLATOR.startDrawing(GL_LINE_LOOP);
            RenderUtil.setTessellatorColor(color);
            TESSELLATOR.addVertex(box.maxX, box.minY, box.maxZ);
            TESSELLATOR.addVertex(box.maxX, box.minY, box.minZ);
            TESSELLATOR.addVertex(box.maxX, box.maxY, box.minZ);
            TESSELLATOR.addVertex(box.maxX, box.maxY, box.maxZ);
            TESSELLATOR.draw();
        }

        if (QuadMask.has(renderMask, QuadMask.EAST))
        {
            TESSELLATOR.startDrawing(GL_LINE_LOOP);
            RenderUtil.setTessellatorColor(color);
            TESSELLATOR.addVertex(box.minX, box.minY, box.minZ);
            TESSELLATOR.addVertex(box.minX, box.minY, box.maxZ);
            TESSELLATOR.addVertex(box.minX, box.maxY, box.maxZ);
            TESSELLATOR.addVertex(box.minX, box.maxY, box.minZ);
            TESSELLATOR.draw();
        }

        if (QuadMask.has(renderMask, QuadMask.DOWN))
        {
            // top
            TESSELLATOR.startDrawing(GL_LINE_LOOP);
            RenderUtil.setTessellatorColor(color);
            TESSELLATOR.addVertex(box.minX, box.maxY, box.maxZ);
            TESSELLATOR.addVertex(box.maxX, box.maxY, box.maxZ);
            TESSELLATOR.addVertex(box.maxX, box.maxY, box.minZ);
            TESSELLATOR.addVertex(box.minX, box.maxY, box.minZ);
            TESSELLATOR.draw();
        }

        glEnable(GL_CULL_FACE);

        glEnable(GL_TEXTURE_2D);

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);

        glLineWidth(1.0f);
        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_BLEND);

        glPopMatrix();
    }

    /**
     * Draws a "billboard" with a callback
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param size the size (0-1)
     * @param callback the callback to render within
     */
    public static void billboard(final double x, final double y, final double z, final double size, final Runnable callback)
    {
        glPushMatrix();

        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(1.0f, -1100000.0f);

        RenderHelper.disableStandardItemLighting();
        glDisable(GL_LIGHTING);

        glTranslated(x - RenderManager.renderPosX, y - RenderManager.renderPosY, z - RenderManager.renderPosZ);
        glRotatef(-RenderManager.instance.playerViewY, 0.0f, 1.0f, 0.0f);
        glRotatef(RenderManager.instance.playerViewX,
                MC.gameSettings.thirdPersonView == 2
                        ? -1.0f
                        : 1.0f,
                0.0f, 0.0f);

        final double scale = (size * Math.max(MC.renderViewEntity.getDistance(x, y, z), 4.0)) / 50.0;
        glScaled(-scale, -scale, scale);

        glDisable(GL_DEPTH_TEST);

        callback.run();

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);

        glPolygonOffset(1.0f, 1100000.0f);
        glDisable(GL_POLYGON_OFFSET_FILL);

        glEnable(GL_ALPHA_TEST);

        glPopMatrix();
    }
}
