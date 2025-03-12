package us.nebula.impl.gui.client.component.cheat.value;

import org.lwjgl.input.Mouse;
import us.nebula.api.gui.GUIComponent;
import us.nebula.api.gui.IGUIInputListener;
import us.nebula.api.gui.font.Fonts;
import us.nebula.api.value.Setting;
import us.nebula.util.io.SoundUtil;
import us.nebula.util.math.MathUtil;
import us.nebula.util.render.RenderUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 03/02/25
 */
public final class NumberSettingComponent extends GUIComponent implements IGUIInputListener
{
    private static final double PADDING = 1.0;

    private static final int TEMP_TOGGLE_COLOR = new Color(112, 82, 143).getRGB();

    private final Setting<Number> setting;
    private boolean dragging;

    public NumberSettingComponent(final Setting<Number> setting)
    {
        this.setting = setting;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (dragging && !Mouse.isButtonDown(0))
        {
            SoundUtil.playClickSound();
            dragging = false;
        }
        if (dragging)
        {
            setValue(mouseX);
        }
        drawSlider();
        drawText();
    }

    private void drawText()
    {
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(setting.getName(), x + (PADDING * 2), y + middle, -1);
        final String value = String.format("%.2f", setting.getValue().doubleValue());
        Fonts.POPPINS.drawStringShadow(value, x + width - Fonts.POPPINS.getStringWidth(value) - (PADDING * 2), y + middle, -1);
    }

    private void drawSlider()
    {
        final double percentage = width * (setting.getValue().doubleValue() / setting.getMax().doubleValue());
        RenderUtil.roundedRectangle2D(x, y, percentage, height, 3.5f, TEMP_TOGGLE_COLOR);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseIn(mouseX, mouseY))
        {
            if (mouseButton == 0 && !dragging)
            {
                SoundUtil.playClickSound();
                dragging = true;
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }

    private void setValue(final int mouseX)
    {
        if (mouseX > x + width || mouseX < x)
        {
            return;
        }
        // think of the start of the slider = 0.0 and the end is = 1.0
        final double diff = 1.0 - ((x + width) - mouseX) / width;
        double value = setting.getMax().doubleValue() * diff;

        final double precision = 1.0 / setting.getScale().doubleValue();
        value = Math.round(value * precision) / precision;
        value = MathUtil.round(value, 2);

        if (setting.getValue() instanceof Integer)
        {
            setting.setValue((int) value);
        } else if (setting.getValue() instanceof Long)
        {
            setting.setValue((long) value);
        } else if (setting.getValue() instanceof Double)
        {
            setting.setValue(value);
        } else if (setting.getValue() instanceof Float)
        {
            setting.setValue((float) value);
        }
    }
}
