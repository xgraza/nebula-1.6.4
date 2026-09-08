package ez.nebula.client.impl.gui.clickgui.component.value;

import ez.nebula.client.impl.gui.clickgui.component.ComponentWithSetting;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.impl.gui.clickgui.component.IComponentDescription;
import ez.nebula.client.util.render.gui.Render2D;
import ez.nebula.client.util.render.gui.trait.GUIComponent;
import ez.nebula.client.util.render.gui.trait.IGUIInputListener;
import ez.nebula.client.util.render.animation.Animation;
import ez.nebula.client.util.render.animation.AnimationEasing;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.impl.module.render.HUDModule;
import ez.nebula.client.util.io.SoundUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 03/02/25
 */
public final class BooleanSettingComponent extends GUIComponent implements IGUIInputListener, ComponentWithSetting, IComponentDescription
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
        Render2D.roundedRectangle(posX, posY, dimensions, dimensions, 3.5f, BACKGROUND_COLOR);

        // create the filled checkbox thing
        final double factoredDimension = dimensions * animation.getEasedFactor();
        Render2D.roundedRectangle(posX + (dimensions - factoredDimension),
                posY + (dimensions - factoredDimension),
                factoredDimension, factoredDimension,
                3.5f, HUDModule.INSTANCE.getPrimary());
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

    @Override
    public boolean isVisible()
    {
        return setting.isVisible();
    }

    @Override
    public Setting<Boolean> getSetting()
    {
        return setting;
    }

    @Override
    public String getDescription()
    {
        return setting.getDescription();
    }
}
