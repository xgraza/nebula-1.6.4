package net.minecraft.src;

import java.awt.Rectangle;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;

public class TooltipProviderOptions implements TooltipProvider
{
    public Rectangle getTooltipBounds(GuiScreen guiScreen, int x, int y)
    {
        int x1 = guiScreen.width / 2 - 150;
        int y1 = guiScreen.height / 6 - 7;

        if (y <= y1 + 98)
        {
            y1 += 105;
        }

        int x2 = x1 + 150 + 150;
        int y2 = y1 + 84 + 10;
        return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    public boolean isRenderBorder()
    {
        return false;
    }

    public String[] getTooltipLines(GuiButton btn, int width)
    {
        if (!(btn instanceof IOptionControl))
        {
            return null;
        }
        else
        {
            IOptionControl ctl = (IOptionControl)btn;
            GameSettings.Options option = ctl.getOption();
            String[] lines = getTooltipLines(option.getEnumString());
            return lines;
        }
    }

    public static String[] getTooltipLines(String key)
    {
        ArrayList list = new ArrayList();

        for (int lines = 0; lines < 10; ++lines)
        {
            String lineKey = key + ".tooltip." + (lines + 1);
            String line = Lang.get(lineKey, (String)null);

            if (line == null)
            {
                break;
            }

            list.add(line);
        }

        if (list.size() <= 0)
        {
            return null;
        }
        else
        {
            String[] var5 = (String[])((String[])list.toArray(new String[list.size()]));
            return var5;
        }
    }
}
