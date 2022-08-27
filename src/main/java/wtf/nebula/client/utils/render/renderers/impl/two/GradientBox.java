package wtf.nebula.client.utils.render.renderers.impl.two;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.Renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

public class GradientBox extends Renderer {
    private final double x, y, width, height;
    private final int topColor, bottomColor;

    public GradientBox(double x, double y, double width, double height, int topColor, int bottomColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.topColor = topColor;
        this.bottomColor = bottomColor;
    }

    @Override
    public void render() {

        if (dimension.equals(Dimension.TWO)) {
            glPushMatrix();

            glDisable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 0, 1);

            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();

            RenderEngine.color(topColor);
            tessellator.addVertex(x, y, 0.0);
            tessellator.addVertex(x, y + height, 0.0);
            RenderEngine.color(bottomColor);
            tessellator.addVertex(x + width, y + height, 0.0);
            tessellator.addVertex(x + width, y, 0.0);
            tessellator.draw();

            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glPopMatrix();
        }
    }
}