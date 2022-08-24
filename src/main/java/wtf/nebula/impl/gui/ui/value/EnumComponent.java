package wtf.nebula.impl.gui.ui.value;

import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.impl.gui.ui.Component;
import wtf.nebula.impl.value.Value;

public class EnumComponent extends Component {
    private final Value<Enum> value;

    public EnumComponent(Value<Enum> value) {
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

        int textWidth = mc.fontRenderer.getStringWidth(value.getValue().toString()) + 2;

        mc.fontRenderer.drawStringWithShadow(EnumChatFormatting.GRAY + value.getValue().toString(),
                (int) ((x + width) - textWidth),
                (int) (y + (height / 2) - mc.fontRenderer.FONT_HEIGHT / 2),
                -1);
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isInBounds(mouseX, mouseY)) {
            playClickSound();

            if (button == 0) {
                value.setValue(Value.increase(value.getValue()));
            }

            else if (button == 1) {
                value.setValue(Value.decrease(value.getValue()));
            }
        }
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int state) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
