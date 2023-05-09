package lol.nebula.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class RenderUtils {

    /**
     * The minecraft game instance
     */
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void setColor(int color) {
        float alpha = (color >> 24 & 0xff) / 255.0f;
        float red = (color >> 16 & 0xff) / 255.0f;
        float green = (color >> 8 & 0xff) / 255.0f;
        float blue = (color & 0xff) / 255.0f;

        setColor(red, green, blue, alpha);
    }

    public static void setColor(float r, float g, float b, float a) {
        glColor4f(r, g, b, a);
    }

    /**
     * Adds an alpha value to a color
     * @param color the color
     * @param alpha the alpha value (0-255)
     * @return the color with the added alpha value
     */
    public static int alpha(int color, int alpha) {
        return color & 0x00ffffff | alpha << 24;
    }

    public static void scissor(double x, double y, double width, double height) {
        final ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        final double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        glEnable(GL_SCISSOR_TEST);
        glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public static void endScissor() {
        glDisable(GL_SCISSOR_TEST);
    }

    public static void rect(double x, double y, double width, double height, int color) {
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glDisable(GL_LIGHTING);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);

        setColor(color);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(x, y, 0.0);
        tessellator.addVertex(x, y + height, 0.0);
        tessellator.addVertex(x + width, y + height, 0.0);
        tessellator.addVertex(x + width, y, 0.0);
        tessellator.draw();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public static void gradientRect(double x, double y, double width, double height, int tl, int bl, int tr, int br) {
        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        glDisable(GL_TEXTURE_2D);

        glShadeModel(GL_SMOOTH);

        glBegin(GL_QUADS);
        {
            setColor(tr);
            glVertex2d(x + width, y);
            setColor(tl);
            glVertex2d(x, y);
            setColor(bl);
            glVertex2d(x, y + height);
            setColor(br);
            glVertex2d(x + width, y + height);
        }
        glEnd();

        glShadeModel(GL_FLAT);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public static void triangle(double x, double y, double width, double height, int color) {
        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        glDisable(GL_TEXTURE_2D);

        glEnable(GL_POLYGON_SMOOTH);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        width *= 0.5;

        setColor(color);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL_POLYGON);
        tessellator.addVertex(x, y, 0.0);
        tessellator.addVertex(x - width, y + height, 0.0);
        tessellator.addVertex(x, y + height, 0.0);
        tessellator.addVertex(x + width, y + height, 0.0);
        tessellator.addVertex(x, y, 0.0);
        tessellator.draw();

        glDisable(GL_POLYGON_SMOOTH);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public static void renderTexture(ResourceLocation loc, double x, double y, int w, int h) {
        glPushMatrix();
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        mc.getTextureManager().bindTexture(loc);

        setColor(1.0f, 1.0f, 1.0f, 1.0f);

        // this is from mc 1.8.9 code cause 1.7.2 fucking sucks

        float u = 0.0f;
        float v = 0.0f;

        float f = 1.0F / (float) w;
        float f1 = 1.0F / (float) h;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + h, 0.0D, u * f, (v + (float)h) * f1);
        tessellator.addVertexWithUV(x + w, y + h, 0.0D, (u + (float)w) * f, (v + (float)h) * f1);
        tessellator.addVertexWithUV(x + w, y, 0.0D, (u + (float)w) * f, v * f1);
        tessellator.addVertexWithUV(x, y, 0.0D, u * f, v * f1);
        tessellator.draw();

        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void filledAabb(AxisAlignedBB box) {
        Tessellator tessellator = Tessellator.instance;

        tessellator.startDrawing(GL_QUADS);
        tessellator.addVertex(box.minX, box.minY, box.minZ);
        tessellator.addVertex(box.maxX, box.minY, box.minZ);
        tessellator.addVertex(box.maxX, box.minY, box.maxZ);
        tessellator.addVertex(box.minX, box.minY, box.maxZ);
        tessellator.draw();

        // sides
        tessellator.startDrawing(GL_QUADS);
        tessellator.addVertex(box.minX, box.minY, box.minZ);
        tessellator.addVertex(box.minX, box.minY, box.maxZ);
        tessellator.addVertex(box.minX, box.maxY, box.maxZ);
        tessellator.addVertex(box.minX, box.maxY, box.minZ);
        tessellator.draw();

        tessellator.startDrawing(GL_QUADS);
        tessellator.addVertex(box.maxX, box.minY, box.maxZ);
        tessellator.addVertex(box.maxX, box.minY, box.minZ);
        tessellator.addVertex(box.maxX, box.maxY, box.minZ);
        tessellator.addVertex(box.maxX, box.maxY, box.maxZ);
        tessellator.draw();

        tessellator.startDrawing(GL_QUADS);
        tessellator.addVertex(box.maxX, box.minY, box.minZ);
        tessellator.addVertex(box.minX, box.minY, box.minZ);
        tessellator.addVertex(box.minX, box.maxY, box.minZ);
        tessellator.addVertex(box.maxX, box.maxY, box.minZ);
        tessellator.draw();

        tessellator.startDrawing(GL_QUADS);
        tessellator.addVertex(box.minX, box.minY, box.maxZ);
        tessellator.addVertex(box.maxX, box.minY, box.maxZ);
        tessellator.addVertex(box.maxX, box.maxY, box.maxZ);
        tessellator.addVertex(box.minX, box.maxY, box.maxZ);
        tessellator.draw();

        // top
        tessellator.startDrawing(GL_QUADS);
        tessellator.addVertex(box.minX, box.maxY, box.maxZ);
        tessellator.addVertex(box.maxX, box.maxY, box.maxZ);
        tessellator.addVertex(box.maxX, box.maxY, box.minZ);
        tessellator.addVertex(box.minX, box.maxY, box.minZ);
        tessellator.draw();
    }
}
