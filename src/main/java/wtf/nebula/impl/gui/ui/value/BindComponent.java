package wtf.nebula.impl.gui.ui.value;

import net.minecraft.src.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import wtf.nebula.impl.gui.ui.ClickGUIScreen;
import wtf.nebula.impl.gui.ui.Component;
import wtf.nebula.impl.value.Bind;

public class BindComponent extends Component {
    private final Bind bind;
    private boolean listening = false;

    public BindComponent(Bind bind) {
        super(bind.getName());
        this.bind = bind;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        ClickGUIScreen.disableClose = listening;

        mc.fontRenderer.drawStringWithShadow(
                name,
                (int) (x + 2.3),
                (int) (y + (height / 2) - mc.fontRenderer.FONT_HEIGHT / 2),
                -1);

        String display = listening ? "..." : Keyboard.getKeyName(bind.getValue());
        int textWidth = mc.fontRenderer.getStringWidth(display) + 2;

        mc.fontRenderer.drawStringWithShadow(EnumChatFormatting.GRAY + display,
                (int) ((x + width) - textWidth),
                (int) (y + (height / 2) - mc.fontRenderer.FONT_HEIGHT / 2),
                -1);
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isInBounds(mouseX, mouseY)) {

            if (button == 0) {
                listening = !listening;
            }

            else {
                listening = false;
                bind.setValue(Keyboard.KEY_NONE);
            }

            ClickGUIScreen.disableClose = listening;
        }
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int state) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (listening) {

            if (keyCode == Keyboard.KEY_ESCAPE) {
                bind.setValue(Keyboard.KEY_NONE);
            }

            else {
                bind.setValue(keyCode);
            }

            listening = false;
            ClickGUIScreen.disableClose = false;
        }
    }
}
