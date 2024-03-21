package nebula.client.util.render;

import io.sentry.Sentry;
import nebula.client.Nebula;
import net.minecraft.client.renderer.texture.DynamicTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gavin
 * @since 08/14/23
 */
public class PlayerSkinUtils {

  private static final Map<String, DynamicTexture> playerFaceTextureMap = new HashMap<>();
  private static final Map<String, BufferedImage> usernameImageCache = new HashMap<>();
  private static final String FACE_URL = "https://minotar.net/helm/%s/%s";

  /**
   * Grabs the cached or create the texture ID for the username face image
   * @param username the username
   * @param size the width & height of the outputted image
   * @return the texture id or -1
   */
  public static DynamicTexture face(String username, int size) {
    String usr = username + "_x" + size;

    if (playerFaceTextureMap.containsKey(usr))
      return playerFaceTextureMap.getOrDefault(usr, null);

    if (!usernameImageCache.containsKey(usr)) {
      usernameImageCache.put(usr, null);
      Nebula.EXECUTOR.execute(() -> {
        try {
          URL url = new URL(String.format(FACE_URL, username, size));
          BufferedImage image = ImageIO.read(url);

          usernameImageCache.put(usr, image);
        } catch (IOException e) {
          e.printStackTrace();
          Sentry.captureException(e);
          playerFaceTextureMap.remove(usr);
        }
      });
    } else {
      BufferedImage image = usernameImageCache.get(usr);
      if (image == null) return null;

      DynamicTexture texture = new DynamicTexture(image);
      playerFaceTextureMap.put(usr, texture);
      usernameImageCache.remove(usr);

      return texture;
    }

    return null;
  }
}
