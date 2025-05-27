package us.nebula.impl.gui.client.component.cheat.value.color;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import us.nebula.api.gui.GUIComponent;
import us.nebula.api.gui.IGUIInputListener;
import us.nebula.api.gui.font.Fonts;
import us.nebula.util.render.RenderUtil;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 04/07/25
 */
public final class GradientColorComponent extends GUIComponent implements IGUIInputListener
{
    private final ColorSettingComponent parent;

    public GradientColorComponent(final ColorSettingComponent parent)
    {
        this.parent = parent;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        final Color color = parent.setting.getValue();

        RenderUtil.gradientRectangle2D(x, y, width, height,
                Color.black.getRGB(),
                Color.black.getRGB(),
                color.hashCode(),
                Color.white.getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {

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
}
