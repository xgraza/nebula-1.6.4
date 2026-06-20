package ez.nebula.client.impl.hud;

import ez.nebula.client.api.render.animation.Animation;
import ez.nebula.client.api.render.animation.AnimationEasing;
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
        description = "Shows when the server is not responding, and how long for",
        width = 100)
public final class ServerStatusHUDElement extends HUDElement
{
    private final Animation animation = new Animation(
            AnimationEasing.CUBIC_IN_OUT, 1000.0);

    @Override
    public void init()
    {
        setHeight(Fonts.POPPINS.getFontHeight() + (getPadding() * 2));
    }

    @Override
    public void render(ScaledResolution res)
    {
        final long lastResponse = Nebula.INSTANCE.getServerManager().getLastPacketMS();
        final long timeSince = System.currentTimeMillis() - lastResponse;

        animation.setState(timeSince >= 3000L);

        String text = "Server is now responding";
        if (animation.getState())
        {
            final double secondsUnresponsive = timeSince / 1000.0;
            text = String.format("Server unresponsive for %.1f seconds", secondsUnresponsive);
        }

        double textWidth = Fonts.POPPINS.getStringWidth(text);
        if (animation.getEasedFactor() == 0.0)
        {
            return;
        }
        Fonts.POPPINS.drawStringShadow(text,
                getX() + (getWidth() / 2.0) - getPadding() - (textWidth / 2.0),
                getY() + getPadding() + (-10 * (1.0 - animation.getFactor())),
                HUDModule.INSTANCE.getBaseColor(10));
    }
}
