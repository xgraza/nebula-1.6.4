package us.nebula.client.cheat.gui.component.cheat.value;

import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import us.nebula.client.util.render.gui.GUIComponent;
import us.nebula.client.util.render.gui.IGUIInputListener;
import us.nebula.client.util.render.gui.font.Fonts;
import us.nebula.client.util.value.Setting;
import us.nebula.client.cheat.impl.render.HUDCheat;
import us.nebula.client.util.io.SoundUtil;
import us.nebula.client.util.math.MathUtil;
import us.nebula.client.util.render.RenderUtil;

import static org.lwjgl.input.Keyboard.KEY_LEFT;
import static org.lwjgl.input.Keyboard.KEY_RIGHT;

/**
 * @author xgraza
 * @since 03/02/25
 */
public class NumberSettingComponent extends GUIComponent implements IGUIInputListener
{
    private static final double PADDING = 1.0;

    protected final Setting<Number> setting;
    private final double diff;
    private boolean dragging;

    private int heldDownTicks;

    public NumberSettingComponent(final Setting<Number> setting)
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

        RenderUtil.renderRoundedRectangle(x, y, barWidth, height, 3.5f, HUDCheat.INSTANCE.getPrimary());
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
