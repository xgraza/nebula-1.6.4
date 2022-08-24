package wtf.nebula.util.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;

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

    /**
     * Draws a filled in bounding box
     *
     * Thanks to Gav06, he gave me this method and I don't feel like writing this myself and he let me use it
     *
     * @param box the AxisAlignedBB
     */
    public static void drawFilledBoundingBox(AxisAlignedBB box) {
        Tessellator tessellator = Tessellator.instance;

        // GL settings
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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

        // GL resets
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }
}
