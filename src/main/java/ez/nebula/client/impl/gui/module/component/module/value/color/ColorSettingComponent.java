package ez.nebula.client.impl.gui.module.component.module.value.color;

import ez.nebula.client.impl.gui.module.component.module.ComponentWithSetting;
import ez.nebula.client.impl.gui.module.component.module.value.BooleanSettingComponent;
import ez.nebula.client.api.setting.ColorSetting;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.api.render.trait.GUIComponent;
import ez.nebula.client.api.render.trait.IGUIInputListener;
import ez.nebula.client.api.render.font.Fonts;
import ez.nebula.client.util.render.RenderUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 04/07/25
 */
public final class ColorSettingComponent extends GUIComponent implements IGUIInputListener, ComponentWithSetting
{
    private static final double PADDING = 1.0;

    private boolean opened;
    private final ColorSetting setting;
    final GradientColorComponent gradientColorComponent;

    public ColorSettingComponent(final ColorSetting setting)
    {
        this.setting = setting;

        getChildrenComponentList().add(gradientColorComponent = new GradientColorComponent(this));
        getChildrenComponentList().add(new HueSliderColorComponent(this));
        if (setting.isAllowTransparency())
        {
            getChildrenComponentList().add(new AlphaSliderColorComponent(this));
        }
        if (!setting.isExemptClientSync())
        {
            getChildrenComponentList().add(new BooleanSettingComponent(new Setting.Builder<>("Client Sync", setting.isClientSync())
                    .onValueChanged(setting::setClientSync)
                    .build()));
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        drawMainBox();
        if (!opened)
        {
            return;
        }
        double posY = getY() + height + PADDING;
        for (final GUIComponent component : getChildrenComponentList())
        {
            component.setX(getX() + PADDING);
            component.setY(posY);
            component.setWidth(getWidth() - (PADDING * 2));
            component.setHeight(14.5);

            component.render(mouseX, mouseY, partialTicks);

            posY += component.getHeight() + (PADDING * 2);
        }
    }

    private void drawMainBox()
    {
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(setting.getName(), x + (PADDING * 2), y + middle, -1);

        final Color color = setting.getValue();
        final double dimensions = height - (PADDING * 4);
        RenderUtil.renderRoundedRectangle(getX() + getWidth() - (PADDING * 2) - dimensions,
                getY() + (PADDING * 2),
                dimensions, dimensions,
                2.5f,
                color.getRGB());

        final String name = String.format("#%02X%02X%02X",
                color.getRed(), color.getGreen(), color.getBlue());
        final double boxWidth = Fonts.POPPINS_SMALL.getStringWidth(name) + (PADDING * 4);
        final double boxHeight = Fonts.POPPINS_SMALL.getFontHeight() + (PADDING * 2);

        final double boxPosX = (x + width) - boxWidth - dimensions - (PADDING * 4);
        final double boxPosY = y - (middle - ((boxHeight - (PADDING * 2)) / 2.0));

        RenderUtil.renderRoundedRectangle(boxPosX, boxPosY, boxWidth, boxHeight, 3.5f, new Color(52, 52, 52).getRGB());
        Fonts.POPPINS_SMALL.drawStringShadow(name, boxPosX + (PADDING * 2), boxPosY + PADDING, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (opened)
        {
            for (GUIComponent guiComponent : getChildrenComponentList())
            {
                if (guiComponent instanceof IGUIInputListener)
                {
                    ((IGUIInputListener) guiComponent).mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }

        if (isMouseIn(mouseX, mouseY) && mouseButton == 1)
        {
            opened = !opened;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }

    @Override
    public double getHeight()
    {
        double h = super.getHeight();
        if (opened)
        {
            h += PADDING * 2;
            for (final GUIComponent component : getChildrenComponentList())
            {
                h += component.getHeight() + PADDING;
            }
        }
        return h;
    }

    @Override
    public boolean isVisible()
    {
        return setting.isVisible();
    }

    @Override
    public ColorSetting getSetting()
    {
        return setting;
    }
}
