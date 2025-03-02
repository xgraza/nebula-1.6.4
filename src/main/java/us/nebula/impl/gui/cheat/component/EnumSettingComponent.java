package us.nebula.impl.gui.cheat.component;

import us.nebula.api.gui.GUIComponent;
import us.nebula.api.gui.IGUIInputListener;
import us.nebula.api.gui.animation.Animation;
import us.nebula.api.gui.animation.AnimationEasing;
import us.nebula.api.gui.font.AWTFontRenderer;
import us.nebula.api.gui.font.FontUtil;
import us.nebula.api.value.Setting;
import us.nebula.util.RenderUtil;

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

    private final AWTFontRenderer smallFont;
    private final Setting<Enum<?>> setting;

    public EnumSettingComponent(final Setting<Enum<?>> setting)
    {
        this.setting = setting;
        smallFont = FontUtil.getFont("poppins", 12);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        final double middle = FontUtil.getMiddlePoint(height, FontUtil.getFontHeight());
        FontUtil.drawStringShadow(setting.getName(), x + (PADDING * 2), y + 3 + middle, -1);
        drawEnumSelector();
    }

    private void drawEnumSelector()
    {
        final String name = formatEnum(setting.getValue());
        final double length = smallFont.getStringWidth(name);
        final double fontHeight = 9;
        RenderUtil.roundedRectangle2D(x + width - length - (PADDING * 2), y + (PADDING * 3), length + PADDING, fontHeight, 3.5f, BACKGROUND_COLOR);
        smallFont.drawStringShadow(name, x + width - length - (PADDING * 3), y + 3, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseIn(mouseX, mouseY))
        {
            if (mouseButton == 0)
            {
                setting.nextEnum();
            } else if (mouseButton == 1)
            {
                setting.previousEnum();
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }

    private String formatEnum(final Enum<?> e)
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
