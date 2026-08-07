package ez.nebula.client.util.io;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import javax.sound.sampled.*;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author xgraza
 * @since 03/12/25
 */
public final class SoundUtil
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private static final ResourceLocation BUTTON_PRESS_SOUND_LOCATION =
            new ResourceLocation("gui.button.press");
    private static final String NEBULA_SOUND_RESOURCE_LOCATION = "/assets/nebula/sound/";

    public static void playClickSound()
    {
        MC.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(
                BUTTON_PRESS_SOUND_LOCATION, 1.0f));
    }

    public static void gottaLog()
    {
        playSound("i_gotta_log_guys.wav");
    }

    public static void vineBoom()
    {
        playSound("vine_boom.wav");
    }

    public static void playNebulaClickSound()
    {
        playSound("click.wav");
    }

    public static void playSound(final String name)
    {
        final InputStream is = SoundUtil.class.getResourceAsStream(NEBULA_SOUND_RESOURCE_LOCATION + name);
        if (is == null)
        {
            return;
        }
        try (final AudioInputStream ais = AudioSystem.getAudioInputStream(is))
        {
            final Clip clip = AudioSystem.getClip();
            clip.addLineListener(event ->
            {
                if (event.getType() == LineEvent.Type.CLOSE
                        || event.getType() == LineEvent.Type.STOP)
                {
                    clip.close();
                    try
                    {
                        is.close();
                    } catch (IOException e)
                    {

                    }
                }
            });
            clip.open(ais);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void beep()
    {
        Toolkit.getDefaultToolkit().beep();
    }
}
