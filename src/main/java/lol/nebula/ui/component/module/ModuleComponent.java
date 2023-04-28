package lol.nebula.ui.component.module;

import lol.nebula.module.Module;
import lol.nebula.module.visual.Interface;
import lol.nebula.ui.component.Component;
import lol.nebula.util.render.RenderUtils;

import java.awt.*;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class ModuleComponent extends Component {

    private static final Color UNTOGGLED_BG = new Color(35, 35, 35);

    private final Module module;
    private boolean expanded;

    public ModuleComponent(Module module) {
        this.module = module;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.rect(getX(), getY(), getWidth(), getHeight(), (module.isToggled() ? Interface.color.getValue() : UNTOGGLED_BG).getRGB());
        mc.fontRenderer.drawStringWithShadow(
                module.getTag(),
                (int) (getX() + 2.0),
                (int) (getY() + 1.0 + (getHeight() / 2.0) - (mc.fontRenderer.FONT_HEIGHT / 2.0)),
                -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInBounds(mouseX, mouseY)) {
            if (mouseButton == 0) {
                module.setState(!module.isToggled());
            } else if (mouseButton == 1) {
                expanded = !expanded;
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
