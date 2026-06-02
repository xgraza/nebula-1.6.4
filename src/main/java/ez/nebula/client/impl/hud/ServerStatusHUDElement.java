package ez.nebula.client.impl.hud;

import net.minecraft.client.gui.ScaledResolution;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.impl.module.render.HUDModule;
import ez.nebula.client.api.manager.hud.HUDElement;
import ez.nebula.client.api.manager.hud.trait.HUDManifest;
import ez.nebula.client.api.render.font.Fonts;

/**
 * @author xgraza
 * @since 05/21/26
 */
@HUDManifest(name = "ServerStatus",
        description = "Shows when the server is not responding, and how long for")
public final class ServerStatusHUDElement extends HUDElement
{
    @Override
    public void init()
    {
        super.init();
        setHeight(Fonts.POPPINS.getFontHeight() + (getPadding() * 2));
    }

    @Override
    public void render(ScaledResolution res)
    {
        final long lastResponse = Nebula.INSTANCE.getServerManager().getLastPacketMS();
        final long timeSince = System.currentTimeMillis() - lastResponse;
        final String formatted = String.format("Server has not been responding for %.1f second%s.",
                (timeSince / 1000.0),
                timeSince > 1000 ? "" : "s");
        setWidth(Fonts.POPPINS.getStringWidth(formatted) + (getPadding() * 2));
        if (timeSince < 5050L)
        {
            return;
        }
        Fonts.POPPINS.drawStringShadow(formatted, getX() + getPadding(), getY() + getPadding(), HUDModule.INSTANCE.getBaseColor(10));
    }
}
