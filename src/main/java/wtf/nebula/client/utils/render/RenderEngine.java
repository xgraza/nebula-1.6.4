package wtf.nebula.client.utils.render;

import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.Renderer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;

public class RenderEngine {

    public static RenderStack of(Dimension dimension) {
        return new RenderStack(dimension);
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
