package ez.nebula.client.impl.hud;

import ez.nebula.client.core.ClientConfig;
import net.minecraft.client.gui.ScaledResolution;
import ez.nebula.client.BuildConfig;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.api.manager.hud.HUDElement;
import ez.nebula.client.api.manager.hud.trait.HUDManifest;
import ez.nebula.client.impl.module.render.HUDModule;

/**
 * @author xgraza
 * @since 3/23/26
 */
@HUDManifest(name = "Watermark",
        description = "Renders the client watermark",
        x = 2.0, y = 2.0)
public final class WatermarkHUDElement extends HUDElement
{
    @Override
    public void render(final ScaledResolution res)
    {
        final String text = BuildConfig.NAME + " " + ClientConfig.FULL_VERSION;
        setWidth(Fonts.POPPINS.getStringWidth(text) + (getPadding() * 2.0));
        setHeight(Fonts.POPPINS.getFontHeight() + (getPadding() * 2.0));
        Fonts.POPPINS.drawStringShadow(text, x, y, HUDModule.INSTANCE.getBaseColor(10));
    }
}
