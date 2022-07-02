package wtf.nebula.impl.gui.ui.value;

import net.minecraft.src.EnumChatFormatting;
import org.lwjgl.input.Mouse;
import wtf.nebula.impl.gui.ui.Component;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.render.RenderUtil;

import java.awt.*;

public class NumberComponent extends Component {
    private final Value<Number> value;
    private final float diff;

    private boolean dragging = false;

    public NumberComponent(Value<Number> value) {
        super(value.getName());
        this.value = value;

        diff = value.getMax().floatValue() - value.getMin().floatValue();
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        if (dragging) {
            if (!Mouse.isButtonDown(0)) {
                dragging = false;
            }

            else {
                setValue(mouseX);
            }
        }

        // draw bar
        float partMultiplier = (value.getValue().floatValue() - value.getMin().floatValue()) / diff;
        double barWidth = value.getValue().floatValue() <= value.getMin().floatValue() ? 0.0 : width * partMultiplier;

        RenderUtil.drawRect(x, y, barWidth, height, new Color(27, 246, 23, 80).getRGB());

        mc.fontRenderer.drawStringWithShadow(
                name,
                (int) (x + 2.3),
                (int) (y + (height / 2) - mc.fontRenderer.FONT_HEIGHT / 2),
                -1);

        int textWidth = mc.fontRenderer.getStringWidth(value.getValue().toString()) + 2;
        mc.fontRenderer.drawStringWithShadow(EnumChatFormatting.GRAY + value.getValue().toString(),
                (int) ((x + width) - textWidth),
                (int) (y + (height / 2) - mc.fontRenderer.FONT_HEIGHT / 2),
                -1);
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isInBounds(mouseX, mouseY) && button == 0) {
            dragging = true;
            playClickSound();

            // allow us to click-set a value
            setValue(mouseX);
        }
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int state) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    private void setValue(int mouseX) {
        float percent = (float) ((mouseX - x) / width);

        if (value.getValue() instanceof Float) {
            float result = value.getMin().floatValue() + diff * percent;
            value.setValue(Math.round(10.0f * result) / 10.0f);
        }

        else if (value.getValue() instanceof Double) {
            double result = value.getMin().doubleValue() + diff * percent;
            value.setValue(Math.round(10.0 * result) / 10.0);
        }

        else {
            value.setValue(Math.round(value.getMin().intValue() + diff * percent));
        }

        if (value.getValue().floatValue() > value.getMax().floatValue()) {
            value.setValue(value.getMax());
        }

        if (value.getValue().floatValue() < value.getMin().floatValue()) {
            value.setValue(value.getMin());
        }
    }
}
