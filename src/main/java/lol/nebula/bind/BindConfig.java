package lol.nebula.bind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.nebula.Nebula;
import lol.nebula.config.Config;
import lol.nebula.util.system.FileUtils;

import java.io.File;
import java.io.IOException;

import static lol.nebula.config.ConfigManager.ROOT;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class BindConfig extends Config {
    private final BindManager binds;

    public BindConfig(BindManager binds) {
        super(new File(ROOT, "binds.json"));
        this.binds = binds;
    }

    @Override
    public void save() {
        JsonArray array = new JsonArray();
        for (Bind bind : binds.getBindList()) {
            JsonObject bindObject = new JsonObject();

            bindObject.addProperty("tag", bind.getTag());
            bindObject.addProperty("key", bind.getKey());
            bindObject.addProperty("state", bind.isToggled());
            bindObject.addProperty("device", bind.getDevice().name());

            array.add(bindObject);
        }

        try {
            FileUtils.write(getFile(), FileUtils.getGSON().toJson(array));
        } catch (IOException e) {
            Nebula.getLogger().error("Failed to write binds");
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        // do not try to read a non-existent file
        if (!getFile().exists()) return;

        String content;
        try {
            content = FileUtils.read(getFile());
        } catch (IOException e) {
            Nebula.getLogger().error("Failed to read binds file");
            e.printStackTrace();

            return;
        }

        // parse json array
        JsonArray array = FileUtils.getGSON().fromJson(content, JsonArray.class);
        if (array == null) return;

        // go through each one
        for (JsonElement element : array) {

            // get the object for this bind
            if (!element.isJsonObject()) continue;
            JsonObject bindObject = element.getAsJsonObject();

            // get the bind object based off the saved tag
            if (!bindObject.has("tag")) continue;

            Bind bind = binds.getBind(bindObject.get("tag").getAsString());
            if (bind == null) continue;

            // save states etc
            bind.setKey(bindObject.get("key").getAsInt());
            bind.setState(bindObject.get("state").getAsBoolean());
            bind.setDevice(BindDevice.valueOf(bindObject.get("device").getAsString()));
        }
    }
}
