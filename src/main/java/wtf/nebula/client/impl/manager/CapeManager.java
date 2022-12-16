package wtf.nebula.client.impl.manager;

import net.minecraft.util.ResourceLocation;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.utils.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CapeManager {
    public static final ResourceLocation CAPE_RESOURCE = new ResourceLocation("nebula/textures/capes/beta_cape.png");

    // LOL
    private static final String PASTEBIN_LINK = "https://pastebin.com/raw/ruxdsWYk";

    // TODO: use discord rich presence to get user id and use discord user id instead of fetching a username from a pastebin (LOL)
    private final List<String> capeUserNames = new ArrayList<>();

    public CapeManager() {
        fetch();
        Nebula.LOGGER.info("Loaded {} cape users", capeUserNames.size());
    }

    public boolean hasCape(String username) {
        return capeUserNames.contains(username);
    }

    private void fetch() {
        capeUserNames.clear();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(PASTEBIN_LINK).openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);

            connection.connect();

            InputStream stream = connection.getInputStream();
            if (stream != null) {
                String content = FileUtils.read(stream);
                if (content != null && !content.isEmpty()) {
                    for (String ln : content.split("\n")) {
                        if (ln.isEmpty()) {
                            continue;
                        }

                        capeUserNames.add(ln.trim());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
