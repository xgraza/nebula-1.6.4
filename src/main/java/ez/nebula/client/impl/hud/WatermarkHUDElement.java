package ez.nebula.client.impl.hud;

import net.minecraft.client.gui.ScaledResolution;
import ez.nebula.client.BuildConfig;
import ez.nebula.client.core.Environment;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.api.render.font.Fonts;
import ez.nebula.client.api.manager.hud.HUDElement;
import ez.nebula.client.api.manager.hud.HUDManifest;
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
    private final Setting<Boolean> showBuildSetting = builder("Show Type", true)
            .setDescription("Show what kind of version Nebula is on")
            .setVisibility((value) -> BuildConfig.ENV != Environment.STABLE)
            .build();
    private final Setting<Boolean> showGit = builder("Show Git", false)
            .setDescription("If to show what git branch and hash this build is on")
            .build();
    private final Setting<Boolean> showBuildID = builder("Show Build ID", false)
            .setDescription("If to show the current build ID")
            .build();

    @Override
    public void render(final ScaledResolution res)
    {
        final String text = getWatermarkText();
        setWidth(Fonts.POPPINS.getStringWidth(text) + (getPadding() * 2.0));
        setHeight(Fonts.POPPINS.getFontHeight() + (getPadding() * 2.0));
        Fonts.POPPINS.drawStringShadow(text, x, y, HUDModule.INSTANCE.getBaseColor(10));
    }

    private String getWatermarkText()
    {
        String versionString = BuildConfig.NAME + " " + BuildConfig.VERSION;

        if (showBuildSetting.getValue() && BuildConfig.ENV != Environment.STABLE)
        {
            versionString += "-" + BuildConfig.ENV;
        }

        if (showBuildID.getValue())
        {
            if (!showBuildSetting.getValue() || BuildConfig.ENV == Environment.STABLE)
            {
                versionString += "+";
            }
            versionString += BuildConfig.BUILD;
        }

        if (showGit.getValue())
        {
            versionString += "/" + BuildConfig.BRANCH + "-" + BuildConfig.HASH;
        }

        return versionString;
    }
}
