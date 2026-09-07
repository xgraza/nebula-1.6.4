package ez.nebula.client.api.manager.hud2.type;

import ez.nebula.client.api.manager.hud2.HUDElement;
import ez.nebula.client.util.render.font.Fonts;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @author xgraza
 * @since 9/6/26
 */
public abstract class TextHUDElement extends HUDElement
{
    @Override
    public void render(final ScaledResolution res)
    {
        final String text = text();
        setWidth(Fonts.POPPINS.getStringWidth(text));
        setHeight(Fonts.POPPINS.getFontHeight());
        Fonts.POPPINS.drawStringShadow(text, x, y, textColor());
    }

    public abstract String text();

    public int textColor()
    {
        return -1;
    }
}
