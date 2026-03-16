package us.nebula.client.impl.gui.client.component.cheat.value;

import us.nebula.client.api.gui.GUIComponent;
import us.nebula.client.api.gui.IGUIInputListener;
import us.nebula.client.api.gui.animation.Animation;
import us.nebula.client.api.gui.animation.AnimationEasing;
import us.nebula.client.api.gui.font.Fonts;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.cheat.render.HUDCheat;
import us.nebula.client.util.io.SoundUtil;
import us.nebula.client.util.render.RenderUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 03/02/25
 */
public final class BooleanSettingComponent extends GUIComponent implements IGUIInputListener
{
    private static final double PADDING = 1.0;

    private static final int BACKGROUND_COLOR = new Color(52, 52, 52).getRGB();

    private final Animation animation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 300.0);

    private final Setting<Boolean> setting;

    public BooleanSettingComponent(final Setting<Boolean> setting)
    {
        this.setting = setting;
        animation.setState(setting.getValue());
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        animation.setState(setting.getValue());
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(setting.getName(), x + (PADDING * 2), y + middle, -1);
        drawCheckbox();
    }

    private void drawCheckbox()
    {
        final double dimensions = height - (PADDING * 4);
        final double posX = x + width - dimensions - (PADDING * 2);
        final double posY = y + (PADDING * 2);
        RenderUtil.roundedRectangle2D(posX, posY, dimensions, dimensions, 3.5f, BACKGROUND_COLOR);

        // create the filled checkbox thing
        final double factoredDimension = dimensions * animation.getEasedFactor();
        RenderUtil.roundedRectangle2D(posX + (dimensions - factoredDimension),
                posY + (dimensions - factoredDimension),
                factoredDimension, factoredDimension,
                3.5f, HUDCheat.INSTANCE.getPrimary());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseIn(mouseX, mouseY))
        {
            SoundUtil.playClickSound();
            setting.setValue(!setting.getValue());
            animation.setState(setting.getValue());
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }
}
