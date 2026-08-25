package ez.nebula.client.impl.gui.module.component.module.value;

import ez.nebula.client.impl.gui.module.component.module.ComponentWithSetting;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.render.gui.Render2D;
import ez.nebula.client.util.render.gui.trait.GUIComponent;
import ez.nebula.client.util.render.gui.trait.IGUIInputListener;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.api.manager.key.Key;
import ez.nebula.client.util.io.SoundUtil;

import java.awt.Color;

import static ez.nebula.client.api.manager.key.Key.DEFAULT_UNBOUND_KEY;

/**
 * @author xgraza
 * @since 03/20/25
 */
public final class KeySettingComponent extends GUIComponent implements IGUIInputListener, ComponentWithSetting
{
    private static final double PADDING = 1.0;

    private static final int KEY_BACKGROUND_COLOR = new Color(33, 33, 33).getRGB();

    private final Setting<Key> setting;
    private final String name;
    private final Key key;
    
    private boolean listeningForKey;

    public KeySettingComponent(final String name, final Key key, final Setting<Key> setting)
    {
        this.name = name;
        this.key = key;
        this.setting = setting;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        final double middle = Fonts.getMiddlePoint(height, Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(name, x + (PADDING * 2), y + middle, -1);
        renderBindBox(middle);
    }

    private void renderBindBox(final double middlePoint)
    {
        final String text = listeningForKey ? "Listening..." : key.toString();
        final double boxWidth = Fonts.POPPINS_SMALL.getStringWidth(text) + (PADDING * 4);
        final double boxHeight = Fonts.POPPINS_SMALL.getFontHeight() + (PADDING * 2);

        final double boxPosX = (x + width) - boxWidth - (PADDING * 2);
        final double boxPosY = y - (middlePoint - ((boxHeight - (PADDING * 2)) / 2.0));

        Render2D.roundedRectangle(boxPosX, boxPosY, boxWidth, boxHeight, 3.5f, KEY_BACKGROUND_COLOR);
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
                key.setMouseBind(false);
                key.setKeyCode(DEFAULT_UNBOUND_KEY);
            }
            return;
        }
        if (listeningForKey)
        {
            listeningForKey = false;
            key.setMouseBind(true);
            key.setKeyCode(mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        if (listeningForKey)
        {
            listeningForKey = false;
            key.setMouseBind(false);
            key.setKeyCode(keyCode);
        }
    }

    @Override
    public boolean isVisible()
    {
        return setting == null || setting.isVisible();
    }

    @Override
    public Setting<Key> getSetting()
    {
        return setting;
    }
}
