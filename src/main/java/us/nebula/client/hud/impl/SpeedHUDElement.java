package us.nebula.client.hud.impl;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import us.nebula.client.cheat.impl.render.HUDCheat;
import us.nebula.client.hud.HUDElement;
import us.nebula.client.hud.HUDManifest;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.util.player.ChatUtil;
import us.nebula.client.util.render.gui.font.Fonts;

/**
 * @author xgraza
 * @since 05/21/26
 */
@HUDManifest(name = "Speed",
        description = "Displays your speed in BPS",
        x = 2, y = 2)
public final class SpeedHUDElement extends HUDElement
{
    private double speed;

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        final double deltaX = MC.thePlayer.posX - MC.thePlayer.lastTickPosX;
        final double deltaZ = MC.thePlayer.posZ - MC.thePlayer.lastTickPosZ;
        final double moveDelta = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        speed = (moveDelta / 1000) / (0.05 / 3600);
        speed *= MC.timer.timerSpeed;
        speed /= 3.6;
    };

    @Override
    public void init()
    {
        super.init();
        setHeight(Fonts.POPPINS.getFontHeight() + (getPadding() * 2));
    }

    @Override
    public void render(final ScaledResolution res)
    {
        final String formatted = String.format("Speed: %s%.2f BPS",
                EnumChatFormatting.GRAY, speed);
        setWidth(Fonts.POPPINS.getStringWidth(formatted) + (getPadding() * 4));
        Fonts.POPPINS.drawStringShadow(formatted, getX() + getPadding(), getY() + getPadding(), HUDCheat.INSTANCE.getBaseColor(10));
    }
}
