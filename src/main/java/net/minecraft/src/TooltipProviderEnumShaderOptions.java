package net.minecraft.src;

import java.awt.Rectangle;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import shadersmod.client.EnumShaderOption;
import shadersmod.client.GuiButtonEnumShaderOption;

public class TooltipProviderEnumShaderOptions implements TooltipProvider
{
    public Rectangle getTooltipBounds(GuiScreen guiScreen, int x, int y)
    {
        int x1 = guiScreen.width - 450;
        int y1 = 35;

        if (x1 < 10)
        {
            x1 = 10;
        }

        if (y <= y1 + 94)
        {
            y1 += 100;
        }

        int x2 = x1 + 150 + 150;
        int y2 = y1 + 84 + 10;
        return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    public boolean isRenderBorder()
    {
        return true;
    }

    public String[] getTooltipLines(GuiButton btn, int width)
    {
        if (!(btn instanceof GuiButtonEnumShaderOption))
        {
            return null;
        }
        else
        {
            GuiButtonEnumShaderOption gbeso = (GuiButtonEnumShaderOption)btn;
            EnumShaderOption option = gbeso.getEnumShaderOption();
            String[] lines = this.getTooltipLines(option);
            return lines;
        }
    }

    private String[] getTooltipLines(EnumShaderOption option)
    {
        return TooltipProviderOptions.getTooltipLines(option.getResourceKey());
    }
}
