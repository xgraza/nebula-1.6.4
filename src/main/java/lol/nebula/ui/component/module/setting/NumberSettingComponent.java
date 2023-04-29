package lol.nebula.ui.component.module.setting;

import lol.nebula.module.visual.Interface;
import lol.nebula.setting.Setting;
import lol.nebula.ui.component.Component;
import lol.nebula.util.math.MathUtils;
import lol.nebula.util.render.RenderUtils;
import org.lwjgl.input.Mouse;

import java.awt.*;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class NumberSettingComponent extends Component {
    private static final Color SETTING_BG = new Color(19, 19, 19);

    private final Setting<Number> setting;

    private final double difference;
    private boolean dragging = false;

    public NumberSettingComponent(Setting<Number> setting) {
        this.setting = setting;
        this.difference = setting.getMax().doubleValue() - setting.getMin().doubleValue();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (dragging) {
            if (!Mouse.isButtonDown(0) || !isInBounds(mouseX, mouseY, getX() - 1.0, getY(), getWidth() + 2, getHeight())) {
                dragging = false;
            }

            if (dragging) {
                setValue(mouseX);
            }
        }

        double part = (setting.getValue().doubleValue() - setting.getMin().doubleValue()) / difference;
        double barWidth = setting.getValue().doubleValue() < setting.getMin().doubleValue() ? 0.0 : (getWidth() - 2.0) * part;

        RenderUtils.rect(getX(), getY(), getWidth(), getHeight(), SETTING_BG.getRGB());
        RenderUtils.rect(getX(), getY(), barWidth, getHeight(), Interface.color.getValue().getRGB());

        mc.fontRenderer.drawStringWithShadow(
                setting.getTag(),
                (int) (getX() + 2.0),
                (int) (getY() + 1.0 + (getHeight() / 2.0) - (mc.fontRenderer.FONT_HEIGHT / 2.0)),
                -1);

        String formatted = setting.getValue().toString();
        mc.fontRenderer.drawStringWithShadow(
                formatted,
                (int) ((getX() + getWidth() - 2.0) - mc.fontRenderer.getStringWidth(formatted)),
                (int) (getY() + 1.0 + (getHeight() / 2.0) - (mc.fontRenderer.FONT_HEIGHT / 2.0)),
                0xBBBBBB);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInBounds(mouseX, mouseY) && mouseButton == 0) {
            dragging = true;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    private void setValue(double mouseX) {
        double value = setting.getMin().doubleValue() + difference * (mouseX - getX()) / getWidth();
        double percision = 1.0 / setting.getScale().doubleValue();
        value = Math.round(value * percision) / percision;
        value = MathUtils.round(value, 2);

        if (value > setting.getMax().doubleValue()) {
            value = setting.getMax().doubleValue();
        }

        if (value < setting.getMin().doubleValue()) {
            value = setting.getMin().doubleValue();
        }

        if (setting.getValue() instanceof Integer) {
            setting.setValue((int) value);
        } else if (setting.getValue() instanceof Double) {
            setting.setValue(value);
        } else if (setting.getValue() instanceof Float) {
            setting.setValue((float) value);
        }
    }
}
