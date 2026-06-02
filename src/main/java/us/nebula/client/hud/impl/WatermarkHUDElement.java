package us.nebula.client.hud.impl;

import net.minecraft.client.gui.ScaledResolution;
import us.nebula.client.ClientSettings;
import us.nebula.client.setting.Setting;
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
        x = 2.0, y = 2.0)
public final class WatermarkHUDElement extends HUDElement
{
    private final Setting<Boolean> fullVersionSetting = builder("Full Version", false)
            .setDescription("If to show the full version with the git branch and hash, plus the build id")
            .build();

    @Override
    public void render(final ScaledResolution res)
    {
        final String text = ClientSettings.NAME + " " + (fullVersionSetting.getValue()
                ? ClientSettings.VERSION
                : ClientSettings.SHORT_VERSION);
        setWidth(Fonts.POPPINS.getStringWidth(text) + (getPadding() * 2.0));
        setHeight(Fonts.POPPINS.getFontHeight() + (getPadding() * 2.0));
        Fonts.POPPINS.drawStringShadow(text, x, y, HUDCheat.INSTANCE.getBaseColor(10));
    }
}
