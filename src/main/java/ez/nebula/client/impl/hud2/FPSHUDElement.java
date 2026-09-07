package ez.nebula.client.impl.hud2;

import ez.nebula.client.api.manager.hud2.trait.HUDManifest;
import ez.nebula.client.api.manager.hud2.type.TextHUDElement;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author xgraza
 * @since 9/6/26
 */
@HUDManifest(value = "FPS", description = "Displays the amount of frames per second rendered on screen")
public final class FPSHUDElement extends TextHUDElement
{
    @Override
    public String text()
    {
        return EnumChatFormatting.NEBULA_CLIENT_COLOR
                + "FPS: "
                + EnumChatFormatting.GRAY
                + Minecraft.debugFPS
                + (!MC.inGameHasFocus ? " [idle]" : "");
    }
}
