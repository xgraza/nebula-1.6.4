package nebula.client.util.system;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class AudioUtils {

  private static final ResourceLocation BUTTON_PRESS_LOCATION = new ResourceLocation(
    "gui.button.press");

  /**
   * The minecraft game instance
   */
  private static final Minecraft mc = Minecraft.getMinecraft();

  public static void click() {
    mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(
      BUTTON_PRESS_LOCATION, 1.0F));
  }
}
