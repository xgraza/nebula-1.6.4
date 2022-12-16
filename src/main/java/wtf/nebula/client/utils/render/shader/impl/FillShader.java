package wtf.nebula.client.utils.render.shader.impl;

import wtf.nebula.client.utils.render.shader.Shader;

import java.awt.*;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static wtf.nebula.client.utils.client.Wrapper.mc;

public class FillShader extends Shader {
    private Color color = Color.white;
    private float lineWidth = 1.5f;
    private float opacity = 0.5f;

    public FillShader() {
        super("/assets/minecraft/nebula/shaders/fill.fsh");
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
        glUniform2f(getUniform("texelSize"), 1F / mc.displayWidth, 1F / mc.displayHeight);
        glUniform4f(getUniform("color"), color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
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
