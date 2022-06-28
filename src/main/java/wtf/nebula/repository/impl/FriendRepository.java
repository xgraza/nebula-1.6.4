package wtf.nebula.repository.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.src.EntityPlayer;
import wtf.nebula.repository.BaseRepository;
import wtf.nebula.repository.Repositories;
import wtf.nebula.util.FileUtil;

import java.nio.file.Files;

import static wtf.nebula.util.FileUtil.FRIENDS;

public class FriendRepository extends BaseRepository<String> {
    @Override
    public void init() {
        if (!Files.exists(FRIENDS)) {
            FileUtil.write(FRIENDS, "[]");

            log.info("Created friends.json");
        }

        else {
            String[] friends = new Gson().fromJson(FileUtil.read(FRIENDS), String[].class);
            for (String fren : friends) {
                addChild(fren);
            }

            log.info("Added " + children.size() + " friend(s).");
        }
    }

    @Override
    public void addChild(String child) {
        children.add(child);
    }

    public boolean isFriend(EntityPlayer player) {
        return children.stream().anyMatch((username) -> player.getEntityName().equals(username));
    }

    public void save() {
        JsonArray array = new JsonArray();
        children.forEach((child) -> array.add(new JsonPrimitive(child)));

        FileUtil.write(FRIENDS, array.toString());
    }

    public static FriendRepository get() {
        return Repositories.getRepo(FriendRepository.class);
    }
}
