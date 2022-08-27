package wtf.nebula.client.utils.render.renderers.impl.two;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.Renderer;

import static org.lwjgl.opengl.GL11.*;

public class Box extends Renderer {
    private final double x, y, width, height;
    private final int color;

    public Box(double x, double y, double width, double height, int color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public void render() {

        if (dimension.equals(Dimension.TWO)) {
            glPushMatrix();

            glDisable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 0, 1);

            RenderEngine.color(color);

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
    }
}
