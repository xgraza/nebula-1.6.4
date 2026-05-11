package us.nebula.client.hud.impl;

import net.minecraft.client.gui.ScaledResolution;
import us.nebula.client.ClientSettings;
import us.nebula.client.util.render.gui.font.Fonts;
import us.nebula.client.hud.HUDElement;
import us.nebula.client.hud.HUDManifest;
import us.nebula.client.cheat.impl.render.HUDCheat;

/**
 * @author xgraza
 * @since 3/23/26
 */
@HUDManifest(name = "Watermark",
        description = "Renders the client watermark",
        x = 2.0,
        y = 2.0)
public final class WatermarkHUDElement extends HUDElement
{
    @Override
    public void render(final ScaledResolution res)
    {
        super.render(res);
        final String text = "Nebula " + ClientSettings.VERSION;
        setWidth(Fonts.POPPINS.getStringWidth(text) + (getPadding() * 2.0));
        setHeight(Fonts.POPPINS.getFontHeight() + (getPadding() * 2.0));
        Fonts.POPPINS.drawStringShadow(text, x, y, HUDCheat.INSTANCE.getBaseColor(10));
    }
}
