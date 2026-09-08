package ez.nebula.client.impl.gui.clickgui.component.module;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.setting.ColorSetting;
import ez.nebula.client.api.setting.EnumSetting;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.impl.gui.clickgui.component.IComponentDescription;
import ez.nebula.client.impl.gui.clickgui.component.value.*;
import ez.nebula.client.util.render.gui.Render2D;
import ez.nebula.client.util.render.gui.trait.GUIComponent;
import ez.nebula.client.util.render.gui.trait.IGUIInputListener;
import ez.nebula.client.util.render.animation.Animation;
import ez.nebula.client.util.render.animation.AnimationEasing;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.api.manager.key.Key;
import ez.nebula.client.api.setting.block.BlockSetting;
import ez.nebula.client.api.setting.block.BlockValue;
import ez.nebula.client.impl.module.render.HUDModule;
import ez.nebula.client.impl.gui.clickgui.component.value.color.ColorSettingComponent;
import ez.nebula.client.util.io.SoundUtil;

import java.awt.Color;
import java.io.File;
import java.util.List;

/**
 * @author xgraza
 * @since 03/01/25
 */
@SuppressWarnings("unchecked")
public final class ModuleComponent extends GUIComponent implements IGUIInputListener, IComponentDescription
{
    private static final double PADDING = 1.0;

    private static final int KEY_BACKGROUND_COLOR = new Color(33, 33, 33).getRGB();
    private static final int BACKGROUND_COLOR = new Color(41, 41, 41).getRGB();
    private static final int PANEL_BACKGROUND_COLOR = new Color(48, 48, 48).getRGB();

    private final Animation hoverAnimation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 350.0);
    private final Animation panelAnimation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 150.0);

    private final Module module;

    public ModuleComponent(final Module module)
    {
        this.module = module;

        getChildrenComponentList().add(new KeySettingComponent("Bind", module.getKey(), null));

        for (final Setting<?> setting : module.getSettings())
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
            } else if (setting.getValue() instanceof BlockValue)
            {
                getChildrenComponentList().add(new BlockSettingComponent((BlockSetting) setting));
            } else if (setting.getValue() instanceof String)
            {
                getChildrenComponentList().add(new StringSettingComponent((Setting<String>) setting));
            } else if (setting.getValue() instanceof List)
            {

            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        hoverAnimation.setState(isMouseIn(mouseX, mouseY));

        Render2D.roundedRectangle(x, y, width, getHeight(), 1.5f,
                module.isToggled() ? HUDModule.INSTANCE.getPrimary() : PANEL_BACKGROUND_COLOR);
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(module.getManifest().name(),
                x + (PADDING * 4) + (2.5 * hoverAnimation.getEasedFactor()),
                y + middle,
                -1);

        final double offset = renderThreeDots();
        renderBindBox(offset, middle);

        if (offset > 0.0 && panelAnimation.getFactor() > 0.0)
        {
            Render2D.roundedRectangle(x + PADDING, y + height, width - (PADDING * 2), getHeight() - height - PADDING, 4f, BACKGROUND_COLOR);

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
        if (getChildrenComponentList().size() <= 2)
        {
            return PADDING * 2;
        }
        final double threeDotsTextWidth = Fonts.TYPEFACE.getStringWidth("g");
        Fonts.TYPEFACE.drawStringShadow("g",
                x + width - (PADDING * 4) - threeDotsTextWidth,
                y + Fonts.getMiddlePoint(height, 6), -1);
        return threeDotsTextWidth + (PADDING * 6);
    }

    private void renderBindBox(final double offset, final double middlePoint)
    {
        final Key key = module.getKey();
        if (key.isUnbound())
        {
            return;
        }
        final String text = key.toString();
        final double boxWidth = Fonts.POPPINS_SMALL.getStringWidth(text) + (PADDING * 4);
        final double boxHeight = Fonts.POPPINS_SMALL.getFontHeight() + (PADDING * 2);

        final double boxPosX = (x + width) - boxWidth - offset;
        final double boxPosY = y - (middlePoint - ((boxHeight - (PADDING * 2)) / 2.0));

        Render2D.roundedRectangle(boxPosX, boxPosY, boxWidth, boxHeight, 4.5f, KEY_BACKGROUND_COLOR);
        Fonts.POPPINS_SMALL.drawStringShadow(text, boxPosX + (PADDING * 2), boxPosY + PADDING, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseIn(mouseX, mouseY))
        {
            if (mouseButton == 0)
            {
                module.toggle();
                SoundUtil.playClickSound();
            } else if (mouseButton == 1)
            {
                panelAnimation.setState(!panelAnimation.getState());
            }
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

    @Override
    public boolean isOpen()
    {
        return panelAnimation.getFactor() != 0.0;
    }

    @Override
    public String getDescription()
    {
        return module.getManifest().description();
    }
}
