package us.nebula.impl.gui.overlay;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import us.nebula.api.gui.font.Fonts;
import us.nebula.api.manager.overlay.Overlay;
import us.nebula.api.manager.overlay.OverlayManifest;
import us.nebula.api.manager.overlay.StaticPosition;

/**
 * @author xgraza
 * @since 05/22/25
 */
@StaticPosition
@OverlayManifest(value = "Coordinates", defaultState = true)
public final class CoordinatesOverlay extends Overlay
{
    @Override
    public void render(final ScaledResolution resolution, final float partialTicks)
    {
        double posY = resolution.getScaledHeight_double()
                - 2.0 - Fonts.POPPINS.getFontHeight();
        if (MC.currentScreen instanceof GuiChat)
        {
            posY -= 14.0;
        }

        Fonts.POPPINS.drawStringShadow(getText(), 2.0, posY, -1);
    }

    private String getText()
    {
        final StringBuilder builder = new StringBuilder();

        builder.append(String.format("%sXYZ%s: %.2f, %.2f, %.2f",
                EnumChatFormatting.BLUE,
                EnumChatFormatting.GRAY,
                MC.thePlayer.posX,
                MC.thePlayer.boundingBox.minY,
                MC.thePlayer.posZ));
        builder.append(" ");
        if (MC.thePlayer.dimension != -1)
        {
            builder.append("[");
            builder.append(EnumChatFormatting.RED);
            builder.append(String.format("%.2f", MC.thePlayer.posX / 8.0));
            builder.append(", ");
            builder.append(String.format("%.2f", MC.thePlayer.posZ / 8.0));
            builder.append(EnumChatFormatting.GRAY);
            builder.append("]");
            builder.append(" ");
        }
        builder.append("(");
        builder.append(EnumChatFormatting.GREEN);
        builder.append(getDirection());
        builder.append(EnumChatFormatting.GRAY);
        builder.append(")");

        return builder.toString();
    }

    private String getDirection()
    {
        final int direction = Math.abs(Math.round((MC.thePlayer.rotationYaw + 1.0f) / 45.0f) % 8);
        switch (direction)
        {
            case 0:
                return "S";
            case 1:
                return "SW";
            case 2:
                return "W";
            case 3:
                return "NW";
            case 4:
                return "N";
            case 5:
                return "NE";
            case 6:
                return "E";
            case 7:
                return "SE";
        }
        return "ERR";
    }
}
