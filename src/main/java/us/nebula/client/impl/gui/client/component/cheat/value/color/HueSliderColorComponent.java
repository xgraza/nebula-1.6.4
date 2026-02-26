package us.nebula.client.impl.gui.client.component.cheat.value.color;

import net.minecraft.util.ResourceLocation;
import us.nebula.client.impl.gui.client.component.cheat.value.NumberSettingComponent;
import us.nebula.client.util.render.RenderUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 1/30/26
 */
public final class HueSliderColorComponent extends NumberSettingComponent
{
    private static final ResourceLocation RGB_GRADIENT_LOCATION = new ResourceLocation(
            "nebula/texture/clickgui/rgb_gradient.png");
    private static final double SLIDER_HEIGHT = 5.0;

    private final ColorSettingComponent parent;

    public HueSliderColorComponent(final ColorSettingComponent parent)
    {
        super(null);
        this.parent = parent;
    }

    @Override
    protected void drawSlider()
    {
        RenderUtil.texture(RGB_GRADIENT_LOCATION, getX(), y, (int) width, (int) getHeight());
        final double position = parent.gradientColorComponent.hsb[0] * getWidth();
        RenderUtil.rectangle2D(getX() + position - 2.5, y, 5, getHeight(), Color.white.getRGB());
    }

    @Override
    protected void drawText()
    {
        // purposefully empty
    }

    @Override
    protected void setValue(int mouseX)
    {
        if (mouseX < getX())
        {
            mouseX = (int) getX();
        }

        if (mouseX > getX() + getWidth())
        {
            mouseX = (int) (getX() + getWidth());
        }

        float h = (float) ((mouseX - getX()) / getWidth());
        parent.gradientColorComponent.hsb[0] = h;
    }

    @Override
    public double getHeight()
    {
        return SLIDER_HEIGHT;
    }
}
