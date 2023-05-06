package lol.nebula.ui.component.module.setting;

import lol.nebula.setting.Setting;
import lol.nebula.ui.component.Component;
import lol.nebula.util.render.RenderUtils;
import lol.nebula.util.render.animation.Animation;
import lol.nebula.util.render.animation.Easing;
import lol.nebula.util.render.font.Fonts;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;

/**
 * @author aesthetical
 * @since 05/06/23
 */
public class ColorSettingComponent extends Component {

    private static final ResourceLocation RGB_GRADIENT = new ResourceLocation("nebula/textures/click/rgb_gradient.png");

    private static final Color SETTING_BG = new Color(19, 19, 19);
    private static final double PADDING = 1.5;

    private static final double PICKER_HEIGHT = 70.0;

    private final Setting<Color> setting;
    private boolean opened;

    private float hue, saturation, brightness;
    private double pickerX, pickerY;
    private double hueX;

    private final Animation openAnimation = new Animation(Easing.CUBIC_IN_OUT, 500, false);

    public ColorSettingComponent(Setting<Color> setting) {
        this.setting = setting;

        Color c = setting.getValue();
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (pickerX == 0.0 && pickerY == 0.0) {
            pickerX = (getX() + PADDING) + (brightness * getWidth());
            pickerY = (getY() + super.getHeight() + 0.5) + ((1.0f - saturation) * PICKER_HEIGHT);
        }

        if (opened && isInBounds(mouseX, mouseY, getX() + PADDING, getY() + super.getHeight() + 0.5, getWidth() - (PADDING * 2), PICKER_HEIGHT)) {

            if (Mouse.isButtonDown(0) && !Mouse.getEventButtonState()) {

                pickerX = mouseX;
                pickerY = mouseY;

                brightness = (float) ((mouseX - getX()) / getWidth());
                saturation = (float) (1.0 - ((mouseY - (getY() + super.getHeight() + 0.5)) / PICKER_HEIGHT));

                setting.setValue(Color.getHSBColor(hue, saturation, brightness));
            }
        }

        if (openAnimation.getState() != opened) {
            openAnimation.setState(opened);
        }

        RenderUtils.rect(getX(), getY(), getWidth(), getHeight(), SETTING_BG.getRGB());
        Fonts.axiforma.drawStringWithShadow(
                setting.getTag(),
                (float) (getX() + 2.0),
                (float) (getY() + (super.getHeight() / 2.0) - (Fonts.axiforma.FONT_HEIGHT / 2.0)),
                -1);

        double size = super.getHeight() - (PADDING * 4);
        Color settingColor = setting.getValue();
        RenderUtils.rect((float) ((getX() + getWidth()) - (PADDING * 6) - 0.5), (float) (getY() + (PADDING * 2)), (float) size, (float) size, settingColor.getRGB());

        if (openAnimation.getFactor() > 0.0) {

            double w = (getWidth() - (PADDING * 2));

            double y = getY() + super.getHeight() + 0.5;
            int s = Color.getHSBColor(hue, 1.0f, 1.0f).getRGB();

            RenderUtils.gradientRect(getX() + PADDING, y, w, PICKER_HEIGHT, Color.black.getRGB(), Color.black.getRGB(), s, Color.white.getRGB());

            double scaledWidth = Fonts.axiforma.getStringWidth("+") / 2.0;
            Fonts.axiforma.drawString("+", pickerX - scaledWidth, pickerY - scaledWidth, -1);

            RenderUtils.renderTexture(RGB_GRADIENT, getX() + PADDING, y + (PICKER_HEIGHT + 5.0), (int) w, 5);

            if (hueX == 0.0) {
                hueX = getX() + PADDING + (hue * getWidth());
            }

            if (hueX + 5.0 >= getX() + w) {
                hueX = getX() + w;
            }

            RenderUtils.triangle(hueX, y + (PICKER_HEIGHT + 4.0), 6.0, 6.0, SETTING_BG.getRGB());

            if (isInBounds(mouseX, mouseY, getX() + PADDING, y + (PICKER_HEIGHT + 5.0), w, 5)) {
                double val = (mouseX - getX()) / getWidth();

                if (Mouse.isButtonDown(0) && !Mouse.getEventButtonState()) {
                    hueX = mouseX;
                    setting.setValue(Color.getHSBColor(hue = (float) val, 1.0f, brightness));
                }
            }

        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInBounds(mouseX, mouseY, getX(), getY(), getWidth(), super.getHeight())) {
            opened = !opened;
        }
    }

    @Override
    public void keyTyped(char charTyped, int keyCode) {

    }

    @Override
    public double getHeight() {
        return super.getHeight() + ((PICKER_HEIGHT + 5.0 + 6.0 + (PADDING)) * openAnimation.getFactor());
    }
}
