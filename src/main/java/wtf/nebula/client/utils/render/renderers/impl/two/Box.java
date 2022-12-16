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
            RenderEngine.rectangle(x, y, width, height, color);
        }
    }
}
