package us.nebula.client.cheat.gui.component.cheat.value;

import us.nebula.client.util.render.gui.GUIComponent;
import us.nebula.client.util.render.gui.IGUIInputListener;
import us.nebula.client.util.render.gui.animation.Animation;
import us.nebula.client.util.render.gui.animation.AnimationEasing;
import us.nebula.client.util.render.gui.font.Fonts;
import us.nebula.client.util.value.Setting;
import us.nebula.client.util.io.SoundUtil;
import us.nebula.client.util.render.RenderUtil;

import java.awt.Color;
import java.util.StringJoiner;

/**
 * @author xgraza
 * @since 03/02/25
 */
public final class EnumSettingComponent extends GUIComponent implements IGUIInputListener
{
    private static final double PADDING = 1.0;

    private static final int BACKGROUND_COLOR = new Color(52, 52, 52).getRGB();

    private final Animation animation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 250);

    private final Setting<Enum<?>> setting;

    public EnumSettingComponent(final Setting<Enum<?>> setting)
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
        final String name = formatEnum(setting.getValue());
        final double boxWidth = Fonts.POPPINS_SMALL.getStringWidth(name) + (PADDING * 4);
        final double boxHeight = Fonts.POPPINS_SMALL.getFontHeight() + (PADDING * 2);

        final double boxPosX = (x + width) - boxWidth - (PADDING * 2);
        final double boxPosY = y - (middlePoint - ((boxHeight - (PADDING * 2)) / 2.0));

        RenderUtil.roundedRectangle2D(boxPosX, boxPosY, boxWidth, boxHeight, 3.5f, BACKGROUND_COLOR);
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
                setting.nextEnum();
            } else if (mouseButton == 1)
            {
                SoundUtil.playClickSound();
                setting.previousEnum();
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

    public static String formatEnum(final Enum<?> e)
    {
        if (!e.toString().equals(e.name()))
        {
            return e.toString();
        }
        final StringJoiner joiner = new StringJoiner(" ");
        for (final String word : e.toString().split("_"))
        {
            joiner.add(Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase());
        }
        return joiner.toString();
    }
}
