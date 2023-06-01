package lol.nebula.ui.click.component.module.setting;

import lol.nebula.setting.Setting;
import lol.nebula.ui.click.component.Component;
import lol.nebula.util.render.RenderUtils;
import lol.nebula.util.render.font.Fonts;

import java.awt.*;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class EnumSettingComponent extends Component {
    private static final Color SETTING_BG = new Color(19, 19, 19);

    private final Setting<Enum<?>> setting;

    public EnumSettingComponent(Setting<Enum<?>> setting) {
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

        String formatted = Setting.formatEnumName(setting.getValue());
        Fonts.axiforma.drawStringWithShadow(
                formatted,
                (float) ((getX() + getWidth() - 4.0) - Fonts.axiforma.getStringWidth(formatted)),
                (float) (getY() + (getHeight() / 2.0) - (Fonts.axiforma.FONT_HEIGHT / 2.0)),
                0xBBBBBB);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInBounds(mouseX, mouseY)) {
            if (mouseButton == 0) {
                setting.next();
            } else if (mouseButton == 1) {
                setting.previous();
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public boolean isVisible() {
        return setting.isVisible();
    }
}
