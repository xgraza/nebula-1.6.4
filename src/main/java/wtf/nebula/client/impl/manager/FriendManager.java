package wtf.nebula.client.impl.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.minecraft.entity.player.EntityPlayer;
import wtf.nebula.client.api.config.Config;
import wtf.nebula.client.utils.client.Wrapper;
import wtf.nebula.client.utils.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendManager implements Wrapper {
    private final List<String> friends = new ArrayList<>();

    public FriendManager() {
        new Config("friends.json") {

            @Override
            public void load(String element) {
                if (element == null || element.isEmpty()) {
                    return;
                }

                JsonArray array = new JsonParser().parse(element).getAsJsonArray();
                array.forEach((e) -> friends.add(e.getAsString()));
            }

            @Override
            public void save() {
                JsonArray array = new JsonArray();
                friends.forEach((uuid) -> array.add(new JsonPrimitive(uuid)));

                if (!getFile().exists()) {
                    try {
                        getFile().createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                FileUtils.write(getFile(), array.toString());
            }
        };
    }

    public void add(String name) {
        friends.add(name);
    }

    public void add(EntityPlayer player) {
        add(player.getCommandSenderName());
    }

    public void remove(EntityPlayer player) {
        remove(player.getCommandSenderName());
    }

    public void remove(String uuid) {
        friends.remove(uuid);
    }

    public boolean isFriend(EntityPlayer player) {
        return isFriend(player.getCommandSenderName());
    }

    public boolean isFriend(String uuid) {
        return friends.contains(uuid);
    }
}
