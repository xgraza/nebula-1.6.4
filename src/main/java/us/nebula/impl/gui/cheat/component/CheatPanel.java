package us.nebula.impl.gui.cheat.component;

import us.nebula.api.gui.GUIComponent;
import us.nebula.api.gui.IGUIInputListener;
import us.nebula.api.gui.animation.Animation;
import us.nebula.api.gui.animation.AnimationEasing;
import us.nebula.api.gui.font.Fonts;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.key.Key;
import us.nebula.api.value.Setting;
import us.nebula.impl.gui.cheat.component.setting.BooleanSettingComponent;
import us.nebula.impl.gui.cheat.component.setting.EnumSettingComponent;
import us.nebula.impl.gui.cheat.component.setting.NumberSettingComponent;
import us.nebula.util.RenderUtil;

import java.awt.Color;

import static us.nebula.api.manager.key.Key.DEFAULT_UNBOUND_KEY;

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
    private static final int TEMP_TOGGLE_COLOR = new Color(112, 82, 143).getRGB();

    private final Animation hoverAnimation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 350.0);
    private final Animation panelAnimation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 250.0);

    private final Cheat cheat;
    private boolean listeningForKey;

    public CheatPanel(final Cheat cheat)
    {
        this.cheat = cheat;
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
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        hoverAnimation.setState(isMouseIn(mouseX, mouseY));

        if (cheat.isToggled())
        {
            RenderUtil.roundedRectangle2D(x, y, width, getHeight(), 1.5f, TEMP_TOGGLE_COLOR);
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

            double posY = y + height;
            for (final GUIComponent component : getChildrenComponentList())
            {
                component.setX(x + PADDING);
                component.setY(posY);
                component.setWidth(width - (PADDING * 2));
                component.setHeight(height);

                component.render(mouseX, mouseY, partialTicks);

                posY += component.getHeight();
            }
        }
    }

    private double renderThreeDots()
    {
        if (cheat.getSettings().isEmpty())
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
            } else if (mouseButton == 1)
            {
                panelAnimation.setState(!panelAnimation.getState());
            } else if (mouseButton == 2)
            {
                if (listeningForKey)
                {
                    listeningForKey = false;
                    cheat.getKey().setKeyCode(DEFAULT_UNBOUND_KEY);
                    cheat.getKey().setUseMouse(false);
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
            cheat.getKey().setUseMouse(true);
            cheat.getKey().setKeyCode(mouseButton);
            return;
        }
        for (final GUIComponent component : getChildrenComponentList())
        {
            if (component instanceof IGUIInputListener)
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
            cheat.getKey().setUseMouse(false);
            cheat.getKey().setKeyCode(keyCode);
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
                h += component.getHeight();
            }
        }
        return (super.getHeight() + PADDING)
                + (h * panelAnimation.getEasedFactor());
    }
}
