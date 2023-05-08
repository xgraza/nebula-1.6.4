package lol.nebula.util.render.shader;

import java.awt.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class ESPShader extends Shader {
    private Color color = Color.white;
    private float lineWidth = 1.5f;
    private float opacity = 0.5f;

    public ESPShader() {
        super("assets/minecraft/nebula/shader/esp.fsh");
    }

    @Override
    protected void onInitialize() {
        createUniform("texture");
        createUniform("texelSize");
        createUniform("color");
        createUniform("divider");
        createUniform("radius");
        createUniform("opacity");
        createUniform("maxSample");
    }

    public void updateUniforms() {
        glUniform1i(getUniform("texture"), 0);
        glUniform2f(getUniform("texelSize"),
                1.0f / mc.displayWidth,
                1.0f / mc.displayHeight);
        glUniform4f(getUniform("color"),
                color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f);
        glUniform1f(getUniform("radius"), lineWidth);
        glUniform1f(getUniform("opacity"), opacity);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }
}
