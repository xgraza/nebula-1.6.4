package wtf.nebula.client.utils.render.renderers.impl.two;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import wtf.nebula.client.utils.render.renderers.Renderer;

import static org.lwjgl.opengl.GL11.*;

public class TextureRenderer extends Renderer {
    private final ResourceLocation location;
    private double x, y, width, height;
    private float rotation;

    public TextureRenderer(ResourceLocation location, double x, double y, double width, double height, float rotation) {
        this.location = location;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
    }

    @Override
    public void render() {
        glPushMatrix();
        glEnable(GL_BLEND);

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glTranslated(x, y, 0.0);

        glRotatef(rotation, 0, 0, 1);

        mc.getTextureManager().bindTexture(location);
        Gui.func_146110_a(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10);

        glDisable(GL_BLEND);
        glPopMatrix();
    }
}
