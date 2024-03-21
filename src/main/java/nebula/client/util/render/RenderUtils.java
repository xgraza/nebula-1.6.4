package nebula.client.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Gavin
 * @since 08/14/23
 */
public class RenderUtils {

  /**
   * The cached ScaledResolution object from {@link net.minecraft.client.gui.GuiIngame}
   */
  public static ScaledResolution resolution;

  public static void rect(double x, double y, double width, double height, int color) {
    glPushMatrix();

    glDisable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    OpenGlHelper.glBlendFunc(770, 771, 0, 1);

    color(color);

    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();
    tessellator.addVertex(x, y, 0.0);
    tessellator.addVertex(x, y + height, 0.0);
    tessellator.addVertex(x + width, y + height, 0.0);
    tessellator.addVertex(x + width, y, 0.0);
    tessellator.draw();

    glDisable(GL_BLEND);
    glEnable(GL_TEXTURE_2D);
    glPopMatrix();
  }

  public static void gradientRect(double x, double y, double width, double height, int topColor, int bottomColor) {
    glPushMatrix();

    glDisable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    OpenGlHelper.glBlendFunc(770, 771, 0, 1);

    Tessellator tessellator = Tessellator.instance;
    tessellator.startDrawingQuads();

    color(topColor);
    tessellator.addVertex(x, y, 0.0);
    tessellator.addVertex(x, y + height, 0.0);
    color(bottomColor);
    tessellator.addVertex(x + width, y + height, 0.0);
    tessellator.addVertex(x + width, y, 0.0);
    tessellator.draw();

    glDisable(GL_BLEND);
    glEnable(GL_TEXTURE_2D);
    glPopMatrix();
  }

  public static void color(int hex) {
    float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
    float red = (float)(hex >> 16 & 0xFF) / 255.0f;
    float green = (float)(hex >> 8 & 0xFF) / 255.0f;
    float blue = (float)(hex & 0xFF) / 255.0f;

    glColor4f(red, green, blue, alpha);
  }

  /**
   * Renders a texture to the screen
   * @param textureId the texture id
   * @param x the x render coordinate
   * @param y the y render coordinate
   * @param w the width to draw the texture
   * @param h the height to draw the texture
   * @param color the color/tint to give to the rendered texture
   */
  public static void renderTexture(int textureId, double x, double y, int w, int h, Color color) {
    glPushMatrix();
    glEnable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    OpenGlHelper.glBlendFunc(770, 771, 1, 0);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    glBindTexture(GL_TEXTURE_2D, textureId);

    // Minecraft#draw
    float f = 0.00390625f;
    glColor4f(color.getRed() * f, color.getGreen() * f, color.getBlue() * f, color.getAlpha() * f);
    Gui.func_146110_a((int) x, (int) y, 0, 0, w, h, w, h);

    glBindTexture(GL_TEXTURE_2D, 0);

    glDisable(GL_BLEND);
    glPopMatrix();
  }

  /**
   * Renders a texture to the screen
   * @param location the resource location object
   * @param x the x render coordinate
   * @param y the y render coordinate
   * @param w the width to draw the texture
   * @param h the height to draw the texture
   * @param color the color/tint to give to the rendered texture
   */
  public static void renderTexture(ResourceLocation location, double x, double y, int w, int h, Color color) {
    glPushMatrix();
    glEnable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    OpenGlHelper.glBlendFunc(770, 771, 1, 0);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    Minecraft.getMinecraft().getTextureManager().bindTexture(location);

    // Minecraft#draw
    float f = 0.00390625f;
    glColor4f(color.getRed() * f,
      color.getGreen() * f,
      color.getBlue() * f,
      color.getAlpha() * f);
    Gui.func_146110_a((int) x, (int) y, 0, 0, w, h, w, h);

    glBindTexture(GL_TEXTURE_2D, 0);

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

  public static void outlinedAabb(AxisAlignedBB box) {
    Tessellator tessellator = Tessellator.instance;

    tessellator.startDrawing(GL_LINE_STRIP);
    tessellator.addVertex(box.minX, box.minY, box.minZ);
    tessellator.addVertex(box.maxX, box.minY, box.minZ);
    tessellator.addVertex(box.maxX, box.minY, box.maxZ);
    tessellator.addVertex(box.minX, box.minY, box.maxZ);
    tessellator.draw();

    // sides
    tessellator.startDrawing(GL_LINE_STRIP);
    tessellator.addVertex(box.minX, box.minY, box.minZ);
    tessellator.addVertex(box.minX, box.minY, box.maxZ);
    tessellator.addVertex(box.minX, box.maxY, box.maxZ);
    tessellator.addVertex(box.minX, box.maxY, box.minZ);
    tessellator.draw();

    tessellator.startDrawing(GL_LINE_STRIP);
    tessellator.addVertex(box.maxX, box.minY, box.maxZ);
    tessellator.addVertex(box.maxX, box.minY, box.minZ);
    tessellator.addVertex(box.maxX, box.maxY, box.minZ);
    tessellator.addVertex(box.maxX, box.maxY, box.maxZ);
    tessellator.draw();

    tessellator.startDrawing(GL_LINE_STRIP);
    tessellator.addVertex(box.maxX, box.minY, box.minZ);
    tessellator.addVertex(box.minX, box.minY, box.minZ);
    tessellator.addVertex(box.minX, box.maxY, box.minZ);
    tessellator.addVertex(box.maxX, box.maxY, box.minZ);
    tessellator.draw();

    tessellator.startDrawing(GL_LINE_STRIP);
    tessellator.addVertex(box.minX, box.minY, box.maxZ);
    tessellator.addVertex(box.maxX, box.minY, box.maxZ);
    tessellator.addVertex(box.maxX, box.maxY, box.maxZ);
    tessellator.addVertex(box.minX, box.maxY, box.maxZ);
    tessellator.draw();

    // top
    tessellator.startDrawing(GL_LINE_STRIP);
    tessellator.addVertex(box.minX, box.maxY, box.maxZ);
    tessellator.addVertex(box.maxX, box.maxY, box.maxZ);
    tessellator.addVertex(box.maxX, box.maxY, box.minZ);
    tessellator.addVertex(box.minX, box.maxY, box.minZ);
    tessellator.draw();
  }
}
