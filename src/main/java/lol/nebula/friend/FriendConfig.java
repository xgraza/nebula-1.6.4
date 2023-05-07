package lol.nebula.friend;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.nebula.Nebula;
import lol.nebula.config.Config;
import lol.nebula.config.ConfigManager;
import lol.nebula.util.system.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author aesthetical
 * @since 05/01/23
 */
public class FriendConfig extends Config {
    private final FriendManager friends;

    public FriendConfig(FriendManager friends) {
        super(new File(ConfigManager.ROOT, "friends.json"));
        this.friends = friends;
    }

    @Override
    public void save() {
        JsonArray array = new JsonArray();
        for (String friend : friends.getFriendList()) array.add(new JsonPrimitive(friend));
        try {
            FileUtils.write(getFile(), FileUtils.getGSON().toJson(array));
        } catch (IOException e) {
            Nebula.getLogger().error("Failed to save friends");
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        if (!getFile().exists()) return;

        // remove old
        friends.getFriendList().clear();

        String content;
        try {
            content = FileUtils.read(getFile());
        } catch (IOException e) {
            Nebula.getLogger().error("Failed to load friends");
            e.printStackTrace();

            return;
        }

        if (content.isEmpty()) return;

        JsonArray array = FileUtils.getGSON().fromJson(content, JsonArray.class);
        for (JsonElement element : array) {
            if (!element.isJsonPrimitive()) continue;
            friends.addFriend(element.getAsString());
        }
    }
}
