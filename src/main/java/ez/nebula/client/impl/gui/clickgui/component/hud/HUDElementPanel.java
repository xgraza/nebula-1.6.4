package ez.nebula.client.impl.gui.clickgui.component.hud;

import ez.nebula.client.api.manager.hud2.HUDElement;
import ez.nebula.client.api.manager.key.Key;
import ez.nebula.client.api.setting.ColorSetting;
import ez.nebula.client.api.setting.EnumSetting;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.impl.gui.clickgui.component.value.*;
import ez.nebula.client.impl.gui.clickgui.component.value.color.ColorSettingComponent;
import ez.nebula.client.impl.module.render.HUDModule;
import ez.nebula.client.util.io.SoundUtil;
import ez.nebula.client.util.render.animation.Animation;
import ez.nebula.client.util.render.animation.AnimationEasing;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.util.render.gui.Render2D;
import ez.nebula.client.util.render.gui.trait.GUIComponent;
import ez.nebula.client.util.render.gui.trait.IGUIInputListener;

import java.awt.Color;
import java.io.File;

/**
 * @author xgraza
 * @since 3/23/26
 */
public final class HUDElementPanel extends GUIComponent implements IGUIInputListener
{
    private static final double PADDING = 1.0;
    private static final int BACKGROUND_COLOR = new Color(41, 41, 41).getRGB();

    private final HUDElement element;
    private final Animation hoverAnimation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 350.0);
    private final Animation panelAnimation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 250.0);

    @SuppressWarnings("unchecked")
    public HUDElementPanel(final HUDElement element)
    {
        this.element = element;
        for (final Setting<?> setting : element.getSettings())
        {
            if (setting.getValue() instanceof Boolean)
            {
                getChildrenComponentList().add(new BooleanSettingComponent((Setting<Boolean>) setting));
            } else if (setting.getValue() instanceof Enum<?>)
            {
                getChildrenComponentList().add(new EnumSettingComponent((EnumSetting<?>) setting));
            } else if (setting.getValue() instanceof Number)
            {
                getChildrenComponentList().add(new NumberSettingComponent((NumberSetting<?>) setting));
            } else if (setting.getValue() instanceof Key)
            {
                getChildrenComponentList().add(new KeySettingComponent(setting.getName(), (Key) setting.getValue(), (Setting<Key>) setting));
            } else if (setting.getValue() instanceof Color)
            {
                getChildrenComponentList().add(new ColorSettingComponent((ColorSetting) setting));
            } else if (setting.getValue() instanceof File)
            {
                final File baseDirectory = ((File) setting.getValue()).getParentFile();
                if (baseDirectory.exists() && baseDirectory.isDirectory())
                {
                    getChildrenComponentList().add(new FileSettingComponent(baseDirectory, (Setting<File>) setting));
                }
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        hoverAnimation.setState(isMouseIn(mouseX, mouseY));

        if (element.isToggled())
        {
            Render2D.roundedRectangle(x, y, width, getHeight(), 1.5f, HUDModule.INSTANCE.getPrimary());
        }
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(element.getManifest().value(),
                x + (PADDING * 4) + (2.5 * hoverAnimation.getEasedFactor()),
                y + middle,
                -1);

        final double offset = renderThreeDots();

        if (offset > 0.0 && panelAnimation.getFactor() > 0.0)
        {
            Render2D.rectangleOutline(x + PADDING, y + height, width - (PADDING * 2), getHeight() - height - PADDING, 4f, BACKGROUND_COLOR);

            double posY = y + height + PADDING;
            for (final GUIComponent component : getChildrenComponentList())
            {
                if (!component.isVisible())
                {
                    continue;
                }
                component.setX(x + (PADDING * 2));
                component.setY(posY);
                component.setWidth(width - (PADDING * 4));
                component.setHeight(height);

                component.render(mouseX, mouseY, partialTicks);

                posY += component.getHeight();
            }
        }
    }

    private double renderThreeDots()
    {
        if (getChildrenComponentList().isEmpty())
        {
            return PADDING * 2;
        }
        final double threeDotsTextWidth = Fonts.TYPEFACE.getStringWidth("g");
        Fonts.TYPEFACE.drawStringShadow("g",
                x + width - (PADDING * 4) - threeDotsTextWidth,
                y + Fonts.getMiddlePoint(height, 6), -1);
        return threeDotsTextWidth + (PADDING * 6);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseIn(mouseX, mouseY))
        {
            if (mouseButton == 0)
            {
                element.toggle();
                SoundUtil.playClickSound();
            } else if (mouseButton == 1)
            {
                if (childrenComponentList.isEmpty())
                {
                    return;
                }
                panelAnimation.setState(!panelAnimation.getState());
            }
            return;
        }
        // do not send listeners if not open
        if (!panelAnimation.getState())
        {
            return;
        }
        for (final GUIComponent component : getChildrenComponentList())
        {
            if (component instanceof IGUIInputListener && component.isVisible())
            {
                ((IGUIInputListener) component).mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        if (!panelAnimation.getState())
        {
            return;
        }
        for (final GUIComponent component : getChildrenComponentList())
        {
            if (component instanceof IGUIInputListener && component.isVisible())
            {
                ((IGUIInputListener) component).keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public double getHeight()
    {
        double h = 0.0;
        if (panelAnimation.getFactor() > 0.0)
        {
            for (final GUIComponent component : getChildrenComponentList())
            {
                if (!component.isVisible())
                {
                    continue;
                }
                h += component.getHeight();
            }
        }
        return height + ((h + (PADDING * 3)) * panelAnimation.getEasedFactor());
    }
}
