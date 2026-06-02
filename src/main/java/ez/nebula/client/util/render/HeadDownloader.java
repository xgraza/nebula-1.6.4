package ez.nebula.client.util.render;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.client.renderer.texture.DynamicTexture;
import ez.nebula.client.core.Nebula;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author xgraza
 * @since 03/26/25
 */
public final class HeadDownloader
{
    private static final Pattern USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9_]{1,16}$");
    private static final String IMAGE_URL = "https://minotar.net/helm/%s/%s.png";
    private static final String DEFAULT_HEAD_NAME = "MHF_Steve";

    private static final Set<String> DOWNLOADING = new ConcurrentSet<>();
    private static final Map<String, BufferedImage> DOWNLOAD_THREAD_MAP = new ConcurrentHashMap<>();
    private static final Map<String, DynamicTexture> TEXTURE_CACHE_MAP = new ConcurrentHashMap<>();

    public static DynamicTexture getOrDownloadTexture(final String name, final int size)
    {
        final String id = name + "_" + size;
        if (DOWNLOADING.contains(id))
        {
            final BufferedImage image = DOWNLOAD_THREAD_MAP.get(id);
            if (image == null)
            {
                return null;
            }
            if (!TEXTURE_CACHE_MAP.containsKey(id))
            {
                final DynamicTexture texture = new DynamicTexture(image);
                TEXTURE_CACHE_MAP.put(id, texture);
                DOWNLOAD_THREAD_MAP.remove(id);
                DOWNLOADING.remove(id);
                return texture;
            }
        }
        final DynamicTexture texture = TEXTURE_CACHE_MAP.get(id);
        if (texture == null)
        {
            DOWNLOADING.add(id);
            Nebula.INSTANCE.getExecutor().execute(() ->
            {
                try
                {
                    final URL url = new URL(String.format(IMAGE_URL,
                            isValidUsername(name)
                                    ? name
                                    : DEFAULT_HEAD_NAME,
                            size));
                    final BufferedImage image = ImageIO.read(url);
                    DOWNLOAD_THREAD_MAP.put(id, image);
                } catch (final IOException e)
                {
                    DOWNLOADING.remove(id);
                    throw new RuntimeException(e);
                }
            });
            return null;
        }
        return texture;
    }

    private static boolean isValidUsername(final String username)
    {
        return username.matches(USERNAME_REGEX.pattern());
    }
}
