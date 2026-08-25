package ez.nebula.client.impl.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.impl.module.render.HUDModule;
import ez.nebula.client.api.manager.hud.HUDElement;
import ez.nebula.client.api.manager.hud.trait.HUDManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.render.font.Fonts;

/**
 * @author xgraza
 * @since 05/21/26
 */
@HUDManifest(name = "TPS",
        description = "Displays the server average TPS",
        x = 2, y = 2)
public final class TPSHUDElement extends HUDElement
{
    private final Setting<Boolean> currentSetting = builder("Show Current", true)
            .setDescription("If to show the current TPS")
            .build();

    @Override
    public void init()
    {
        super.init();
        setHeight(Fonts.POPPINS.getFontHeight() + (getPadding() * 2));
    }

    @Override
    public void render(final ScaledResolution res)
    {
        String formatted = String.format("TPS: %s%.2f",
                EnumChatFormatting.GRAY, Nebula.INSTANCE.getServerManager().getAverageTPS());
        if (currentSetting.getValue())
        {
            formatted += " [" + String.format("%.2f", Nebula.INSTANCE.getServerManager().getCurrentTPS()) + "]";
        }
        setWidth(Fonts.POPPINS.getStringWidth(formatted) + (getPadding() * 4));
        Fonts.POPPINS.drawStringShadow(formatted, getX() + getPadding(), getY() + getPadding(), HUDModule.INSTANCE.getBaseColor(10));
    }
}
