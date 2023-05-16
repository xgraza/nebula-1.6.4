package lol.nebula.module.visual.xray;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.nebula.config.Config;
import lol.nebula.module.visual.XRay;
import lol.nebula.util.system.FileUtils;

import java.io.File;
import java.io.IOException;

import static lol.nebula.config.ConfigManager.ROOT;

/**
 * @author aesthetical
 * @since 05/16/23
 */
public class XrayBlocksConfig extends Config {
    public XrayBlocksConfig() {
        super(new File(ROOT, "blocks.txt"));
    }

    @Override
    public void save() {
        JsonArray array = new JsonArray();
        for (int id : XRay.BLOCK_LIST) array.add(new JsonPrimitive(id));

        try {
            FileUtils.write(getFile(), array.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        if (!getFile().exists()) return;

        String content;
        try {
            content = FileUtils.read(getFile());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (content.isEmpty()) return;

        XRay.BLOCK_LIST.clear();

        JsonArray array = FileUtils.getGSON().fromJson(content, JsonArray.class);
        for (JsonElement element : array) {
            if (!element.isJsonPrimitive()) continue;
            XRay.BLOCK_LIST.add(element.getAsInt());
        }
    }
}
