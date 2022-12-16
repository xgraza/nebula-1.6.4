package wtf.nebula.client.utils.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import wtf.nebula.client.utils.client.Wrapper;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.Renderer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;

public class RenderEngine implements Wrapper {

    public static RenderStack of(Dimension dimension) {
        return new RenderStack(dimension);
    }

    public static void renderTexture(ResourceLocation location, double x, double y, int u, int v, double width, double height) {
        glPushMatrix();
        glEnable(GL_BLEND);

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        //glTranslated(x, y, 0.0);

        mc.getTextureManager().bindTexture(location);
        Gui.drawScaledCustomSizeModalRect((int) x, (int) y, (float) u, (float) v, (int) width, (int) height, (int) width, (int) height, 64, 32);
        //Gui.func_146110_a((int) x, (int) y, (float) u, (float) v, (int) width, (int) height, (float) width, (float) height);

        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void rectangle(double x, double y, double width, double height, int color) {
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

    public static void color(int hex) {
        float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;

        glColor4f(red, green, blue, alpha);
    }

    public static float calculateRotation(float var0) {
        if ((var0 %= 360.0F) >= 180.0F) {
            var0 -= 360.0F;
        }

        if (var0 < -180.0F) {
            var0 += 360.0F;
        }

        return var0;
    }

    public static class RenderStack {
        private final List<Renderer> renderers = new CopyOnWriteArrayList<>();
        private final Dimension dimension;

        public RenderStack(Dimension dimension) {
            this.dimension = dimension;
        }

        public RenderStack add(Renderer renderer) {
            renderer.setDimension(dimension);
            renderers.add(renderer);
            return this;
        }

        public void render() {
            glPushMatrix();

            renderers.forEach(Renderer::render);

            glPopMatrix();
        }
    }
}
