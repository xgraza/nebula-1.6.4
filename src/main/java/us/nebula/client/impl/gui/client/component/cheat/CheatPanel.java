package us.nebula.client.impl.gui.client.component.cheat;

import us.nebula.client.api.gui.GUIComponent;
import us.nebula.client.api.gui.IGUIInputListener;
import us.nebula.client.api.gui.animation.Animation;
import us.nebula.client.api.gui.animation.AnimationEasing;
import us.nebula.client.api.gui.font.Fonts;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.key.Key;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.cheat.render.HUDCheat;
import us.nebula.client.impl.gui.client.component.cheat.value.*;
import us.nebula.client.impl.gui.client.component.cheat.value.color.ColorSettingComponent;
import us.nebula.client.util.io.SoundUtil;
import us.nebula.client.util.render.RenderUtil;

import java.awt.Color;
import java.io.File;

import static us.nebula.client.api.manager.key.Key.DEFAULT_UNBOUND_KEY;

/**
 * @author xgraza
 * @since 03/01/25
 */
@SuppressWarnings("unchecked")
public final class CheatPanel extends GUIComponent implements IGUIInputListener
{
    private static final double PADDING = 1.0;

    private static final int KEY_BACKGROUND_COLOR = new Color(33, 33, 33).getRGB();
    private static final int BACKGROUND_COLOR = new Color(41, 41, 41).getRGB();

    private final Animation hoverAnimation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 350.0);
    private final Animation panelAnimation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 250.0);

    private final Cheat cheat;
    private boolean listeningForKey;

    public CheatPanel(final Cheat cheat)
    {
        this.cheat = cheat;
        getChildrenComponentList().add(new BooleanSettingComponent(
                new Setting<Boolean>("Hidden", cheat.isHidden())
                {
                    @Override
                    public void setValue(final Boolean value)
                    {
                        cheat.setHidden(value);
                    }

                    @Override
                    public Boolean getValue()
                    {
                        return cheat.isHidden();
                    }
                }));
        for (final Setting<?> setting : cheat.getSettings())
        {
            if (setting.getValue() instanceof Boolean)
            {
                getChildrenComponentList().add(new BooleanSettingComponent((Setting<Boolean>) setting));
            } else if (setting.getValue() instanceof Enum<?>)
            {
                getChildrenComponentList().add(new EnumSettingComponent((Setting<Enum<?>>) setting));
            } else if (setting.getValue() instanceof Number)
            {
                getChildrenComponentList().add(new NumberSettingComponent((Setting<Number>) setting));
            } else if (setting.getValue() instanceof Key)
            {
                getChildrenComponentList().add(new KeySettingComponent((Setting<Key>) setting));
            } else if (setting.getValue() instanceof Color)
            {
                getChildrenComponentList().add(new ColorSettingComponent((Setting<Color>) setting));
            } else if (setting.getValue() instanceof File || setting.getBaseDirectory() != null)
            {
                getChildrenComponentList().add(new FileSettingComponent((Setting<File>) setting));
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        hoverAnimation.setState(isMouseIn(mouseX, mouseY));

        if (cheat.isToggled())
        {
            RenderUtil.roundedRectangle2D(x, y, width, getHeight(), 1.5f, HUDCheat.INSTANCE.getPrimary());
        }
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(cheat.getManifest().name(),
                x + (PADDING * 4) + (2.5 * hoverAnimation.getEasedFactor()),
                y + middle,
                -1);

        final double offset = renderThreeDots();
        renderBindBox(offset, middle);

        if (offset > 0.0 && panelAnimation.getFactor() > 0.0)
        {
            RenderUtil.roundedRectangle2D(x + PADDING, y + height, width - (PADDING * 2), getHeight() - height - PADDING, 4f, BACKGROUND_COLOR);

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
        if (getChildrenComponentList().size() <= 1)
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
        final Key key = cheat.getKey();
        if (key.isUnbound() && !listeningForKey)
        {
            return;
        }
        final String text = listeningForKey ? "Listening..." : key.toString();
        final double boxWidth = Fonts.POPPINS_SMALL.getStringWidth(text) + (PADDING * 4);
        final double boxHeight = Fonts.POPPINS_SMALL.getFontHeight() + (PADDING * 2);

        final double boxPosX = (x + width) - boxWidth - offset;
        final double boxPosY = y - (middlePoint - ((boxHeight - (PADDING * 2)) / 2.0));

        RenderUtil.roundedRectangle2D(boxPosX, boxPosY, boxWidth, boxHeight, 3.5f, KEY_BACKGROUND_COLOR);
        Fonts.POPPINS_SMALL.drawStringShadow(text, boxPosX + (PADDING * 2), boxPosY + PADDING, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseIn(mouseX, mouseY))
        {
            if (mouseButton == 0)
            {
                cheat.toggle();
                SoundUtil.playClickSound();
            } else if (mouseButton == 1)
            {
                panelAnimation.setState(!panelAnimation.getState());
            } else if (mouseButton == 2)
            {
                if (listeningForKey)
                {
                    listeningForKey = false;
                    cheat.getKey().setKeyCode(DEFAULT_UNBOUND_KEY);
                    cheat.getKey().setMouseBind(false);
                    return;
                } else
                {
                    listeningForKey = true;
                }
            }
            return;
        }
        if (listeningForKey)
        {
            listeningForKey = false;
            cheat.getKey().setMouseBind(true);
            cheat.getKey().setKeyCode(mouseButton);
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
        if (listeningForKey)
        {
            listeningForKey = false;
            cheat.getKey().setMouseBind(false);
            cheat.getKey().setKeyCode(keyCode);
            return;
        }
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

    public Cheat getCheat()
    {
        return cheat;
    }
}
