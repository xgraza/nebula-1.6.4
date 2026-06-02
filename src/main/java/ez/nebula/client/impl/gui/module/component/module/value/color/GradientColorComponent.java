package ez.nebula.client.impl.gui.module.component.module.value.color;

import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import ez.nebula.client.api.render.trait.GUIComponent;
import ez.nebula.client.api.render.trait.IGUIInputListener;
import ez.nebula.client.util.io.SoundUtil;
import ez.nebula.client.util.render.RenderUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 04/07/25
 */
public final class GradientColorComponent extends GUIComponent implements IGUIInputListener
{
    private final ColorSettingComponent parent;
    private final float[] hsb = new float[3];
    private double pointerX, pointerY;
    private boolean dragging;

    public GradientColorComponent(final ColorSettingComponent parent)
    {
        this.parent = parent;
        final Color color = parent.getSetting().getValue();
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (dragging)
        {
            pointerX = MathHelper.clamp_double(mouseX, x, x + getWidth());
            pointerY = MathHelper.clamp_double(mouseY, y, y + getHeight());
            hsb[1] = (float) (1.0 - ((pointerY - getY()) / getHeight()));
            hsb[2] = (float) ((pointerX - getX()) / getWidth());
            updateColor();
            if (!Mouse.isButtonDown(0))
            {
                dragging = false;
            }
        } else
        {
            updatePointerPos();
        }

        RenderUtil.renderGradientRectangle(x, y, getWidth(), getHeight(),
                Color.black.hashCode(),
                Color.black.hashCode(),
                Color.HSBtoRGB(hsb[0], 1, 1),
                Color.white.hashCode());

        RenderUtil.renderRectangle(pointerX - 2.5, pointerY - 2.5, 5, 5, Color.black.getRGB());
        RenderUtil.render2DOutline(pointerX - 2.5, pointerY - 2.5, 5, 5, 1.5f, Color.white.getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseInDynamic(mouseX, mouseY))
        {
            SoundUtil.playClickSound();
            dragging = true;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }

    @Override
    public double getHeight()
    {
        return getWidth();
    }

    private void updatePointerPos()
    {
        pointerX = getX() + (getWidth() * hsb[2]);
        pointerY = getY() + (getHeight() * (1 - hsb[1]));
    }

    public float getHue()
    {
        return hsb[0];
    }

    public void updateHue(float hue)
    {
        hsb[0] = hue;
        updateColor();
    }

    public void updateTransparency(float alpha)
    {
        parent.getSetting().setTransparency((int) (255 * alpha));
        updateColor();
    }

    private void updateColor()
    {
        final int transparency = parent.getSetting().getValue().getAlpha();
        parent.getSetting().setHSB(hsb[0], hsb[1], hsb[2]);
        parent.getSetting().setTransparency(transparency);
    }
}
