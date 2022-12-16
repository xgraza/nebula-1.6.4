package wtf.nebula.client.utils.render.renderers.impl;

import net.minecraft.client.renderer.OpenGlHelper;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.Renderer;

import static org.lwjgl.opengl.GL11.*;

public class Line extends Renderer {
    private final double x1, y1, z1, x2, y2, z2;
    private final float lineWidth;
    private final int color;

    public Line(double x1, double y1, double z1, double x2, double y2, double z2, float lineWidth, int color) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.lineWidth = lineWidth;
        this.color = color;
    }

    @Override
    public void render() {
        glPushMatrix();

        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        glLineWidth(lineWidth);

        RenderEngine.color(color);

        glBegin(GL_LINES);
        {
            if (dimension.equals(Dimension.THREE)) {
                glVertex3d(x1, y1, z1);
                glVertex3d(x2, y2, z2);
            } else {
                glVertex2d(x1, y1);
                glVertex2d(x2, y2);
            }
        }
        glEnd();

        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);

        glLineWidth(1.0f);
        glDisable(GL_LINE_SMOOTH);

        glPopMatrix();
    }
}
