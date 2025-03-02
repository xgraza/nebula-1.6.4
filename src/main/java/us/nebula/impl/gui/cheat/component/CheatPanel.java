package us.nebula.impl.gui.cheat.component;

import org.lwjgl.input.Keyboard;
import us.nebula.api.gui.GUIComponent;
import us.nebula.api.gui.IGUIInputListener;
import us.nebula.api.gui.animation.Animation;
import us.nebula.api.gui.animation.AnimationEasing;
import us.nebula.api.gui.font.AWTFontRenderer;
import us.nebula.api.gui.font.FontUtil;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.value.Setting;
import us.nebula.util.RenderUtil;

import java.awt.Color;

import static org.lwjgl.input.Keyboard.KEY_NONE;

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
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (cheat.isToggled())
        {
            RenderUtil.roundedRectangle2D(x, y, width, getHeight(), 1.5f, TEMP_TOGGLE_COLOR);
        }
        hoverAnimation.setState(isMouseIn(mouseX, mouseY));
        final double middle = FontUtil.getMiddlePoint(height, FontUtil.getFontHeight());
        FontUtil.drawStringShadow(cheat.getManifest().name(), x + (PADDING * 2) + (2.5 * hoverAnimation.getEasedFactor()),
                y + 3 + middle,
                -1);

        if (cheat.getKey().getKeyCode() != KEY_NONE || listeningForKey)
        {
            String keyName = "NONE";
            if (cheat.getKey().isUseMouse())
            {
                keyName = "MOUSE" + (cheat.getKey().getKeyCode() + 1);
            } else
            {
                keyName = Keyboard.getKeyName(cheat.getKey().getKeyCode());
            }
            if (listeningForKey)
            {
                keyName = "Listening...";
            }
            final AWTFontRenderer smallFont = FontUtil.getFont("poppins", 12);
            final double textWidth = smallFont.getStringWidth(keyName);
            RenderUtil.roundedRectangle2D((x + width) - textWidth - 21, y + 3, textWidth + (PADDING * 4), 9, 5f, KEY_BACKGROUND_COLOR);
            smallFont.drawStringShadow(keyName, (x + width) - textWidth - 20, y + 3, -1);
        }
        FontUtil.getFont("icon", 18).drawStringShadow("g", x + width - (PADDING * 16), y + 5 + middle, -1);

        if (panelAnimation.getFactor() > 0.0)
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
                    cheat.getKey().setKeyCode(KEY_NONE);
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
        return super.getHeight() + PADDING
                + (h * panelAnimation.getEasedFactor());
    }
}
