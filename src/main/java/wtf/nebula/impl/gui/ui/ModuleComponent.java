package wtf.nebula.impl.gui.ui;

import net.minecraft.src.EnumChatFormatting;
import wtf.nebula.impl.gui.ui.value.BindComponent;
import wtf.nebula.impl.gui.ui.value.BooleanComponent;
import wtf.nebula.impl.gui.ui.value.EnumComponent;
import wtf.nebula.impl.gui.ui.value.NumberComponent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.value.Bind;

public class ModuleComponent extends Component {
    private final Module module;
    private boolean opened = false;

    public ModuleComponent(Module module) {
        super(module.getName());
        this.module = module;

        module.getValues().forEach((value) -> {
            if (value instanceof Bind) {
                children.add(new BindComponent((Bind) value));
            }

            else {

                if (value.getValue() instanceof Boolean) {
                    children.add(new BooleanComponent(value));
                }

                else if (value.getValue() instanceof Enum) {
                    children.add(new EnumComponent(value));
                }

                else if (value.getValue() instanceof Number) {
                    children.add(new NumberComponent(value));
                }
            }
        });
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        mc.fontRenderer.drawStringWithShadow(
                (module.getState() ? EnumChatFormatting.GREEN.toString() : "") + name,
                (int) (x + 2.3),
                (int) (y + (14.0 / 2) - mc.fontRenderer.FONT_HEIGHT / 2),
                -1);

        if (opened) {
            height = 14.5;

            double startY = y + height;
            for (Component component : children) {
                component.x = x + 2;
                component.y = startY;
                component.width = width - 4;
                component.height = 14.0;

                component.drawComponent(mouseX, mouseY, partialTicks);

                startY += 14.5;
                height += component.height + 0.5;
            }
        }

        else {
            height = 14.0;
        }
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isMouseWithinBounds(mouseX, mouseY, x, y, width, 14.0)) {

            playClickSound();

            if (button == 0) {
                module.setState(!module.getState());
            }

            else if (button == 1) {
                opened = !opened;
            }
        }

        if (opened) {
            children.forEach((child) -> child.mouseClick(mouseX, mouseY, button));
        }
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int state) {
        if (opened) {
            children.forEach((child) -> child.mouseRelease(mouseX, mouseY, state));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (opened) {
            children.forEach((child) -> child.keyTyped(typedChar, keyCode));
        }
    }
}
