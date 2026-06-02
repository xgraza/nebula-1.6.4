package ez.nebula.client.util.io;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

/**
 * @author xgraza
 * @since 03/12/25
 */
public final class SoundUtil
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private static final ResourceLocation BUTTON_PRESS_SOUND_LOCATION =
            new ResourceLocation("gui.button.press");

    public static void playClickSound()
    {
        MC.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(
                BUTTON_PRESS_SOUND_LOCATION, 1.0f));
    }
}
