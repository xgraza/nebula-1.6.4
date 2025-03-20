package us.nebula.impl.gui.client.component.cheat.value;

import us.nebula.api.gui.GUIComponent;
import us.nebula.api.gui.IGUIInputListener;
import us.nebula.api.gui.font.Fonts;
import us.nebula.api.manager.key.Key;
import us.nebula.api.value.Setting;
import us.nebula.util.io.SoundUtil;
import us.nebula.util.render.RenderUtil;

import java.awt.Color;

import static us.nebula.api.manager.key.Key.DEFAULT_UNBOUND_KEY;

/**
 * @author xgraza
 * @since 03/20/25
 */
public final class KeySettingComponent extends GUIComponent implements IGUIInputListener
{
    private static final double PADDING = 1.0;

    private static final int KEY_BACKGROUND_COLOR = new Color(33, 33, 33).getRGB();

    private final Setting<Key> setting;
    private boolean listeningForKey;

    public KeySettingComponent(final Setting<Key> setting)
    {
        this.setting = setting;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(setting.getName(), x + (PADDING * 2), y + middle, -1);
        renderBindBox(middle);
    }

    private void renderBindBox(final double middlePoint)
    {
        final Key key = setting.getValue();
        final String text = listeningForKey ? "Listening..." : key.toString();
        final double boxWidth = Fonts.POPPINS_SMALL.getStringWidth(text) + (PADDING * 4);
        final double boxHeight = Fonts.POPPINS_SMALL.getFontHeight() + (PADDING * 2);

        final double boxPosX = (x + width) - boxWidth;
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
                SoundUtil.playClickSound();
                listeningForKey = !listeningForKey;
            } else if (mouseButton == 2)
            {
                listeningForKey = false;
                setting.getValue().setMouseBind(false);
                setting.getValue().setKeyCode(DEFAULT_UNBOUND_KEY);
            }
            return;
        }
        if (listeningForKey)
        {
            listeningForKey = false;
            setting.getValue().setMouseBind(true);
            setting.getValue().setKeyCode(mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        if (listeningForKey)
        {
            listeningForKey = false;
            setting.getValue().setMouseBind(false);
            setting.getValue().setKeyCode(keyCode);
        }
    }
}
