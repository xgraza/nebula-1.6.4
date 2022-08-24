package wtf.nebula.impl.gui.ui.value;

import wtf.nebula.impl.gui.ui.Component;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.render.RenderUtil;

import java.awt.*;

public class BooleanComponent extends Component {
    private final Value<Boolean> value;

    public BooleanComponent(Value<Boolean> value) {
        super(value.getName());
        this.value = value;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        mc.fontRenderer.drawStringWithShadow(
                name,
                (int) (x + 2.3),
                (int) (y + (height / 2) - mc.fontRenderer.FONT_HEIGHT / 2),
                -1);

        double toggleButtonDimensions = height - 4.0;
        RenderUtil.drawRect(
                x + width - toggleButtonDimensions - 2.0,
                y + 2.0,
                toggleButtonDimensions, toggleButtonDimensions,
                Color.gray.getRGB());

        toggleButtonDimensions -= 2.0;
        RenderUtil.drawRect(
                x + width - toggleButtonDimensions - 3.0,
                y + 3.0,
                toggleButtonDimensions, toggleButtonDimensions,
                value.getValue() ? Color.black.getRGB() : Color.darkGray.getRGB());
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isInBounds(mouseX, mouseY)) {
            playClickSound();
            value.setValue(!value.getValue());
        }
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int state) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
