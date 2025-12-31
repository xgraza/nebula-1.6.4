package us.nebula.client.impl.gui.client.component.cheat.value;

import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import us.nebula.client.api.gui.GUIComponent;
import us.nebula.client.api.gui.IGUIInputListener;
import us.nebula.client.api.gui.font.Fonts;
import us.nebula.client.api.value.Setting;
import us.nebula.client.util.io.SoundUtil;
import us.nebula.client.util.math.MathUtil;
import us.nebula.client.util.render.RenderUtil;

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
    private final double diff;
    private boolean dragging;

    public NumberSettingComponent(final Setting<Number> setting)
    {
        this.setting = setting;
        this.diff = setting.getMax().doubleValue() - setting.getMin().doubleValue();
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
        final double min = setting.getMin().doubleValue();
        final double value = setting.getValue().doubleValue();

        final double barWidth = width * ((value - min) / diff);

        RenderUtil.roundedRectangle2D(x, y, barWidth, height, 3.5f, TEMP_TOGGLE_COLOR);
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
        double value = setting.getMin().doubleValue()
                + diff * (mouseX - getX()) / getWidth();

        final double precision = 1.0 / setting.getScale().doubleValue();
        value = Math.round(value * precision) / precision;
        value = MathUtil.round(value, 2);
        value = MathHelper.clamp_double(value,
                setting.getMin().doubleValue(),
                setting.getMax().doubleValue());

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
