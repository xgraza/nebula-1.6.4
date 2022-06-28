package wtf.nebula.util.render;

import net.minecraft.src.Tessellator;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {

    /**
     * Draws a rectangle with the minecraft tesslleator
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color the rectangle's color
     */
    public static void drawRect(double x, double y, double width, double height, int color) {

        // the tessellator instance
        Tessellator tessellator = Tessellator.instance;

        // GL settings
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // set the rectangle color
        ColorUtil.setColor(color);

        // draw the rectangle
        tessellator.startDrawingQuads();

        // add the rectangle vertices
        tessellator.addVertex(x, y, 0.0);
        tessellator.addVertex(x, y + height, 0.0);
        tessellator.addVertex(x + width, y + height, 0.0);
        tessellator.addVertex(x + width, y, 0.0);

        // draw the vertices
        tessellator.draw();

        // GL resets
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }
}
