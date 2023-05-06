package lol.nebula.ui.component.module.setting;

import lol.nebula.bind.Bind;
import lol.nebula.bind.BindDevice;
import lol.nebula.setting.Setting;
import lol.nebula.ui.component.Component;
import lol.nebula.util.render.RenderUtils;
import lol.nebula.util.render.font.Fonts;
import org.lwjgl.input.Keyboard;

import java.awt.*;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class BindSettingComponent extends Component {
    private static final Color SETTING_BG = new Color(19, 19, 19);

    private final Setting<Bind> setting;
    private boolean listening;

    public BindSettingComponent(Setting<Bind> setting) {
        this.setting = setting;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.rect(getX(), getY(), getWidth(), getHeight(), SETTING_BG.getRGB());

        Fonts.axiforma.drawStringWithShadow(
                listening ? "Listening..." : setting.getTag(),
                (float) (getX() + 2.0),
                (float) (getY() + (super.getHeight() / 2.0) - (Fonts.axiforma.FONT_HEIGHT / 2.0)),
                -1);

        if (!listening) {
            String formatted = formatBind();
            Fonts.axiforma.drawStringWithShadow(
                    formatted,
                    (float) ((getX() + getWidth() - 2.0) - Fonts.axiforma.getStringWidth(formatted)),
                    (float) (getY() + (getHeight() / 2.0) - (Fonts.axiforma.FONT_HEIGHT / 2.0)),
                    0xBBBBBB);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInBounds(mouseX, mouseY)) {
            if (mouseButton == 0) {
                listening = true;
                return;
            } else if (mouseButton == 1) {
                listening = false;
                setting.getValue().setKey(-1);
            }
        }

        if (listening) {
            listening = false;
            setting.getValue().setDevice(BindDevice.MOUSE);
            setting.getValue().setKey(mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (listening) {
            listening = false;
            setting.getValue().setDevice(BindDevice.KEYBOARD);
            setting.getValue().setKey(keyCode == KEY_NONE ? -1 : keyCode);
        }
    }

    private String formatBind() {
        Bind bind = setting.getValue();
        if (bind.getKey() == -1) return "NONE";

        switch (bind.getDevice()) {
            case KEYBOARD:
                return Keyboard.getKeyName(bind.getKey());

            case MOUSE:
                return "MB " + (bind.getKey() + 1);

            default:
            case UNKNOWN:
                return "None";
        }
    }
}
