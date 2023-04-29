package lol.nebula.ui.component.module.setting;

import lol.nebula.bind.Bind;
import lol.nebula.bind.BindDevice;
import lol.nebula.setting.Setting;
import lol.nebula.ui.component.Component;
import lol.nebula.util.render.RenderUtils;
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

        mc.fontRenderer.drawStringWithShadow(
                listening ? "Listening..." : setting.getTag(),
                (int) (getX() + 2.0),
                (int) (getY() + 1.0 + (getHeight() / 2.0) - (mc.fontRenderer.FONT_HEIGHT / 2.0)),
                -1);

        if (!listening) {
            String formatted = formatBind();
            mc.fontRenderer.drawStringWithShadow(
                    formatted,
                    (int) ((getX() + getWidth() - 2.0) - mc.fontRenderer.getStringWidth(formatted)),
                    (int) (getY() + 1.0 + (getHeight() / 2.0) - (mc.fontRenderer.FONT_HEIGHT / 2.0)),
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
                setting.getValue().setKey(KEY_NONE);
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
            setting.getValue().setKey(keyCode);
        }
    }

    private String formatBind() {
        Bind bind = setting.getValue();
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
