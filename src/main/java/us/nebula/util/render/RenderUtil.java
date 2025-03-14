package us.nebula.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
import us.nebula.api.gui.shader.Shader;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class RenderUtil
{
    private static final Minecraft MC = Minecraft.getMinecraft();
    private static final Tessellator TESSELLATOR = Tessellator.instance;

    private static ScaledResolution GAME_RESOLUTION;
    private static Shader ROUNDED_RECTANGLE_SHADER,
            BLUR_SHADER,
            ESP_SHADER;

    public static void initShaders()
    {
        ROUNDED_RECTANGLE_SHADER = new Shader(
                "/assets/minecraft/nebula/shader/vertex.vsh",
                "/assets/minecraft/nebula/shader/roundedrect.frag",
                (shader) ->
                {
                    shader.createUniform("rectSize");
                    shader.createUniform("color");
                    shader.createUniform("radius");
                    shader.createUniform("edgeSoftness");
                });
    }

    public static void startScissor(final double x,
                                    final double y,
                                    final double width,
                                    final double height)
    {
        final double scale = GAME_RESOLUTION.getScaleFactor();
        glEnable(GL_SCISSOR_TEST);
        glScissor((int) (x * scale),
                (int) (((GAME_RESOLUTION.getScaledHeight_double() - y) * scale) - (height * scale)),
                (int) (width * scale),
                (int) (height * scale));
    }

    public static void endScissor()
    {
        glDisable(GL_SCISSOR_TEST);
    }

    public static void filledBox3D(final AxisAlignedBB aabb,
                                   final int renderMask,
                                   final int color)
    {
        glPushMatrix();

        glDisable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);

        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);

        glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

        TESSELLATOR.startDrawingQuads();
        TESSELLATOR.setColorOpaque_I(color);
        TESSELLATOR.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        TESSELLATOR.draw();

        // sides
        TESSELLATOR.startDrawingQuads();
        TESSELLATOR.setColorOpaque_I(color);
        TESSELLATOR.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        TESSELLATOR.draw();

        TESSELLATOR.startDrawingQuads();
        TESSELLATOR.setColorOpaque_I(color);
        TESSELLATOR.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        TESSELLATOR.draw();

        TESSELLATOR.startDrawingQuads();
        TESSELLATOR.setColorOpaque_I(color);
        TESSELLATOR.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        TESSELLATOR.draw();

        TESSELLATOR.startDrawingQuads();
        TESSELLATOR.setColorOpaque_I(color);
        TESSELLATOR.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        TESSELLATOR.draw();

        // top
        TESSELLATOR.startDrawingQuads();
        TESSELLATOR.setColorOpaque_I(color);
        TESSELLATOR.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        TESSELLATOR.draw();

        glEnable(GL_TEXTURE_2D);

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);

        glPopMatrix();
    }

    public static void outlinedBox3D(final AxisAlignedBB aabb,
                                   final float lineWidth,
                                   final int color)
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

        glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

        TESSELLATOR.startDrawing(GL_LINE_STRIP);
        TESSELLATOR.setColorOpaque_I(color);
        TESSELLATOR.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        TESSELLATOR.draw();

        TESSELLATOR.startDrawing(GL_LINE_STRIP);
        TESSELLATOR.setColorOpaque_I(color);
        TESSELLATOR.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        TESSELLATOR.draw();

        TESSELLATOR.startDrawing(GL_LINES);
        TESSELLATOR.setColorOpaque_I(color);
        TESSELLATOR.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        TESSELLATOR.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        TESSELLATOR.draw();

        glEnable(GL_TEXTURE_2D);

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);

        glLineWidth(1.0f);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_BLEND);

        glPopMatrix();
    }

    public static void rectangle2D(final double x,
                                   final double y,
                                   final double width,
                                   final double height,
                                   final int color)
    {
        glDisable(GL_TEXTURE_2D);
        setColor(color);
        TESSELLATOR.startDrawingQuads();
        TESSELLATOR.addVertex(x, y, 0.0);
        TESSELLATOR.addVertex(x, y + height, 0.0);
        TESSELLATOR.addVertex(x + width, y + height, 0.0);
        TESSELLATOR.addVertex(x + width, y, 0.0);
        TESSELLATOR.draw();
        glEnable(GL_TEXTURE_2D);
    }

    public static void roundedRectangle2D(final double x,
                                          final double y,
                                          final double width,
                                          final double height,
                                          final float radius,
                                          final int color)
    {
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);

        ROUNDED_RECTANGLE_SHADER.use(() -> {
            int scaleFactor = MC.gameSettings.guiScale;
            if (GAME_RESOLUTION != null) {
                scaleFactor = GAME_RESOLUTION.getScaleFactor();
            }

            ROUNDED_RECTANGLE_SHADER.set("rectSize", (float) (width * scaleFactor), (float) (height * scaleFactor));
            final float[] argb = getColorARGB(color);
            ROUNDED_RECTANGLE_SHADER.set("color", argb[1], argb[2], argb[3], argb[0]);
            ROUNDED_RECTANGLE_SHADER.set("radius", radius * scaleFactor);
            ROUNDED_RECTANGLE_SHADER.set("edgeSoftness", 1.0f);
        });

        glBegin(GL_QUADS);
        {
            glTexCoord2d(0, 0);
            glVertex2d(x, y);
            glTexCoord2d(0, 1);
            glVertex2d(x, y + height);
            glTexCoord2d(1, 1);
            glVertex2d(x + width, y + height);
            glTexCoord2d(1, 0);
            glVertex2d(x + width, y);
        }
        glEnd();

        ROUNDED_RECTANGLE_SHADER.stop();

        glDisable(GL_BLEND);
    }

    public static float[] getColorARGB(final int color)
    {
        final float alpha = (color >> 24 & 0xff) / 255.0f;
        final float red = (color >> 16 & 0xff) / 255.0f;
        final float green = (color >> 8 & 0xff) / 255.0f;
        final float blue = (color & 0xff) / 255.0f;
        return new float[] { alpha, red, green, blue };
    }

    public static void setColor(final int color)
    {
        final float red = (float) (color >> 16 & 255) / 255.0F;
        final float blue = (float) (color >> 8 & 255) / 255.0F;
        final float green = (float) (color & 255) / 255.0F;
        final float alpha = (float) (color >> 24 & 255) / 255.0F;
        glColor4f(red, green, blue, alpha);
    }

    public static void setGameResolution(final ScaledResolution gameResolution)
    {
        GAME_RESOLUTION = gameResolution;
    }
}
