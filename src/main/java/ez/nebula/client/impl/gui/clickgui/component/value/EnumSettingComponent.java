package ez.nebula.client.impl.gui.clickgui.component.value;

import ez.nebula.client.api.setting.EnumSetting;
import ez.nebula.client.impl.gui.clickgui.component.ComponentWithSetting;
import ez.nebula.client.impl.gui.clickgui.component.IComponentDescription;
import ez.nebula.client.util.io.SoundUtil;
import ez.nebula.client.util.render.animation.Animation;
import ez.nebula.client.util.render.animation.AnimationEasing;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.util.render.gui.Render2D;
import ez.nebula.client.util.render.gui.trait.GUIComponent;
import ez.nebula.client.util.render.gui.trait.IGUIInputListener;
import ez.nebula.client.util.text.FormattingUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 03/02/25
 */
public final class EnumSettingComponent extends GUIComponent implements IGUIInputListener, ComponentWithSetting, IComponentDescription
{
    private static final double PADDING = 1.0;

    private static final int BACKGROUND_COLOR = new Color(52, 52, 52).getRGB();

    private final Animation animation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 250);

    private final EnumSetting<?> setting;

    public EnumSettingComponent(final EnumSetting<?> setting)
    {
        this.setting = setting;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(setting.getName(), x + (PADDING * 2), y + middle, -1);
        drawEnumSelector(middle);
    }

    private void drawEnumSelector(final double middlePoint)
    {
        final String name = FormattingUtil.formatEnum(setting.getValue());
        final double boxWidth = Fonts.POPPINS_SMALL.getStringWidth(name) + (PADDING * 4);
        final double boxHeight = Fonts.POPPINS_SMALL.getFontHeight() + (PADDING * 2);

        final double boxPosX = (x + width) - boxWidth - (PADDING * 2);
        final double boxPosY = y - (middlePoint - ((boxHeight - (PADDING * 2)) / 2.0));

        Render2D.roundedRectangle(boxPosX, boxPosY, boxWidth, boxHeight, 3.5f, BACKGROUND_COLOR);
        Fonts.POPPINS_SMALL.drawStringShadow(name, boxPosX + (PADDING * 2), boxPosY + PADDING, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseIn(mouseX, mouseY))
        {
            if (mouseButton == 0)
            {
                SoundUtil.playClickSound();
                setting.nextValue();
            } else if (mouseButton == 1)
            {
                SoundUtil.playClickSound();
                setting.previousValue();
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }

    @Override
    public boolean isVisible()
    {
        return setting.isVisible();
    }

    @Override
    public EnumSetting<?> getSetting()
    {
        return setting;
    }

    @Override
    public String getDescription()
    {
        return setting.getDescription();
    }
}
