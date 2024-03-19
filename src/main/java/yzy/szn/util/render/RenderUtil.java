package yzy.szn.util.render;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 03/12/24
 */
public final class RenderUtil {

    public static void rect(final double x,
                            final double y,
                            final double width,
                            final double height,
                            final int color) {

        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        setColor(color);

        glBegin(GL_QUADS);
        {
            glVertex2d(x, y); // top left
            glVertex2d(x, y + height); // go to bottom left
            glVertex2d(x + width, y + height); // go to bottom right
            glVertex2d(x + width, y); // top right
        }
        glEnd();

        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();

    }

    public static void outline(final double x,
                               final double y,
                               final double width,
                               final double height,
                               final float lineWidth,
                               final int color) {

        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(lineWidth);

        setColor(color);

        glBegin(GL_LINES);
        {
            glVertex2d(x, y); // top left
            glVertex2d(x, y + height); // go to bottom left

            glVertex2d(x, y + height); // go to bottom left
            glVertex2d(x + width, y + height); // go to bottom right

            glVertex2d(x + width, y + height); // go to bottom right
            glVertex2d(x + width, y); // top right

            glVertex2d(x + width, y); // top right
            glVertex2d(x, y); // top left

        }
        glEnd();

        glLineWidth(1.0F);
        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();

    }

    private static void setColor(final int color) {
        float alpha = (color >> 24 & 0xff) / 255.0f;
        float red = (color >> 16 & 0xff) / 255.0f;
        float green = (color >> 8 & 0xff) / 255.0f;
        float blue = (color & 0xff) / 255.0f;
        glColor4f(red, green, blue, alpha);
    }
}
