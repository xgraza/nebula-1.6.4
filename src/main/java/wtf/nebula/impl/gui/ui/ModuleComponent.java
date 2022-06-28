package wtf.nebula.impl.gui.ui;

import net.minecraft.src.EnumChatFormatting;
import wtf.nebula.impl.module.Module;

public class ModuleComponent extends Component {
    private final Module module;

    public ModuleComponent(Module module) {
        super(module.getName());
        this.module = module;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        mc.fontRenderer.drawStringWithShadow(
                (module.getState() ? EnumChatFormatting.GREEN.toString() : "") + name,
                (int) (x + 4.2),
                (int) (y + (height / 2) - mc.fontRenderer.FONT_HEIGHT / 2),
                -1);
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isInBounds(mouseX, mouseY)) {

            if (button == 0) {
                playClickSound();
                module.setState(!module.getState());
            }

            else {

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
