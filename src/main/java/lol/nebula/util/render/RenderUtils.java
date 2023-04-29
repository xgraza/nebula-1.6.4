package lol.nebula.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

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

        glColor4f(red, green, blue, alpha);
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
}
