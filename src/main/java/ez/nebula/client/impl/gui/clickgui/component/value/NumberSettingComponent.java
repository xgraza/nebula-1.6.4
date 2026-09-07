package ez.nebula.client.impl.gui.clickgui.component.value;

import ez.nebula.client.util.render.gui.Render2D;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import ez.nebula.client.impl.gui.clickgui.component.ComponentWithSetting;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.util.render.gui.trait.GUIComponent;
import ez.nebula.client.util.render.gui.trait.IGUIInputListener;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.impl.module.render.HUDModule;
import ez.nebula.client.util.io.SoundUtil;
import ez.nebula.client.util.math.MathUtil;

import static org.lwjgl.input.Keyboard.KEY_LEFT;
import static org.lwjgl.input.Keyboard.KEY_RIGHT;

/**
 * @author xgraza
 * @since 03/02/25
 */
public class NumberSettingComponent extends GUIComponent implements IGUIInputListener, ComponentWithSetting
{
    private static final double PADDING = 1.0;

    protected final NumberSetting<?> setting;
    private final double diff;
    private boolean dragging;

    private int heldDownTicks;
    private double renderWidth;

    public NumberSettingComponent(final NumberSetting<?> setting)
    {
        this.setting = setting;
        if (setting != null)
        {
            this.diff = setting.getMax().doubleValue() - setting.getMin().doubleValue();
        } else
        {
            this.diff = 0.1f;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (isMouseInDynamic(mouseX, mouseY))
        {
            if (Keyboard.isKeyDown(KEY_RIGHT))
            {
                ++heldDownTicks;
                increaseByScale(true);
            } else if (Keyboard.isKeyDown(KEY_LEFT))
            {
                ++heldDownTicks;
                increaseByScale(false);
            } else
            {
                heldDownTicks = 0;
            }
        }

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

    protected void drawText()
    {
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(setting.getName(), x + (PADDING * 2), y + middle, -1);
        final String value = String.format("%.2f", setting.getValue().doubleValue());
        Fonts.POPPINS.drawStringShadow(value, x + width - Fonts.POPPINS.getStringWidth(value) - (PADDING * 2), y + middle, -1);
    }

    protected void drawSlider()
    {
        final double min = setting.getMin().doubleValue();
        final double value = setting.getValue().doubleValue();

        final double barWidth = width * ((value - min) / diff);
        renderWidth += (barWidth - renderWidth) * 0.15;
        if (Math.abs(renderWidth - barWidth) <= 0.01)
        {
            renderWidth = barWidth;
        }
        Render2D.roundedRectangle(x, y, renderWidth, height, 3.5f, HUDModule.INSTANCE.getPrimary());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseInDynamic(mouseX, mouseY))
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

    @Override
    public boolean isVisible()
    {
        return setting.isVisible();
    }

    @SuppressWarnings("unchecked")
    protected void increaseByScale(boolean direction)
    {
        if (heldDownTicks <= 60 && heldDownTicks != 1)
        {
            return;
        }

        final double scale = setting.getScale().doubleValue();

        if ((direction && setting.getValue().doubleValue() + scale >= setting.getMax().doubleValue())
                || (!direction && setting.getValue().doubleValue() - scale <= setting.getMin().doubleValue()))
        {
            return;
        }

        final double value = setting.getValue().doubleValue() + (direction ? scale : -scale);

        if (setting.getValue() instanceof Integer)
        {
            ((NumberSetting<Integer>) setting).setValue((int) value);
        } else if (setting.getValue() instanceof Long)
        {
            ((NumberSetting<Long>) setting).setValue((long) value);
        } else if (setting.getValue() instanceof Double)
        {
            ((NumberSetting<Double>) setting).setValue(value);
        } else if (setting.getValue() instanceof Float)
        {
            ((NumberSetting<Float>) setting).setValue((float) value);
        }
    }

    @SuppressWarnings("unchecked")
    protected void setValue(final int mouseX)
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
            ((NumberSetting<Integer>) setting).setValue((int) value);
        } else if (setting.getValue() instanceof Long)
        {
            ((NumberSetting<Long>) setting).setValue((long) value);
        } else if (setting.getValue() instanceof Double)
        {
            ((NumberSetting<Double>) setting).setValue(value);
        } else if (setting.getValue() instanceof Float)
        {
            ((NumberSetting<Float>) setting).setValue((float) value);
        }
    }

    @Override
    public NumberSetting<?> getSetting()
    {
        return setting;
    }
}
