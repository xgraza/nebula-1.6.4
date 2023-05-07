package lol.nebula.ui.click.component.module.setting;

import lol.nebula.module.visual.Interface;
import lol.nebula.setting.Setting;
import lol.nebula.ui.click.component.Component;
import lol.nebula.util.render.RenderUtils;
import lol.nebula.util.render.font.Fonts;

import java.awt.*;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class BooleanSettingComponent extends Component {
    private static final Color SETTING_BG = new Color(19, 19, 19);

    private final Setting<Boolean> setting;

    public BooleanSettingComponent(Setting<Boolean> setting) {
        this.setting = setting;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.rect(getX(), getY(), getWidth(), getHeight(), SETTING_BG.getRGB());
        Fonts.axiforma.drawStringWithShadow(
                setting.getTag(),
                (float) (getX() + 2.0),
                (float) (getY() + (super.getHeight() / 2.0) - (Fonts.axiforma.FONT_HEIGHT / 2.0)),
                -1);

        double dimension = getHeight() - 4.0;
        RenderUtils.rect(getX() + getWidth() - 2.0 - dimension, getY() + 2.0, dimension, dimension, (setting.getValue() ? Interface.color.getValue() : SETTING_BG.brighter()).getRGB());

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInBounds(mouseX, mouseY) && mouseButton == 0) {
            setting.setValue(!setting.getValue());
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
