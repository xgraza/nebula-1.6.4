package wtf.nebula.repository.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import net.minecraft.entity.player.EntityPlayer;
import wtf.nebula.repository.BaseRepository;
import wtf.nebula.repository.Repositories;
import wtf.nebula.repository.Repository;
import wtf.nebula.util.FileUtil;

import java.nio.file.Files;

import static wtf.nebula.util.FileUtil.FRIENDS;

@Repository("Friends")
public class FriendRepository extends BaseRepository<String> {
    @Override
    public void init() {
        if (!Files.exists(FRIENDS)) {
            FileUtil.write(FRIENDS, "[]");

            log.info("Created friends.json");
        }

        else {

            try {
                String[] friends = new Gson().fromJson(FileUtil.read(FRIENDS), String[].class);
                for (String fren : friends) {
                    addChild(fren);
                }

                log.info("Added " + children.size() + " friend(s).");
            } catch (Exception e) {
                e.printStackTrace();
                log.fatal("An error occurred when parsing the friends.json file. Please make sure to not make any external edits.");
            }
        }
    }

    @Override
    public void addChild(String child) {
        children.add(child);
    }

    public void removeChild(String child) {
        children.removeIf(child::equals);
    }

    public boolean isFriend(String name) {
        return children.stream().anyMatch(name::equals);
    }

    public boolean isFriend(EntityPlayer player) {
        return isFriend(player.getCommandSenderName());
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
