package wtf.nebula.client.utils.render.renderers.impl;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.Vec3;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.Renderer;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Lines extends Renderer {
    private final List<Vec3> lines;
    private final float lineWidth;
    private final int color;

    public Lines(List<Vec3> lines, float lineWidth, int color) {
        this.lines = lines;
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

        glBegin(GL_LINE_STRIP);
        {
            for (Vec3 vec : lines) {

                double x = vec.xCoord;
                double y = vec.yCoord;
                double z = vec.zCoord;

                if (dimension.equals(Dimension.THREE)) {
                    glVertex3d(x, y, z);
                } else {
                    glVertex2d(x, y);
                }
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
