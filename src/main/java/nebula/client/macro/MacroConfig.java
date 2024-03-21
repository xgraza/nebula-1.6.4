package nebula.client.macro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nebula.client.Nebula;
import nebula.client.config.Config;
import nebula.client.util.fs.FileUtils;
import nebula.client.util.fs.JSONUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Gavin
 * @since 08/09/23
 */
public class MacroConfig implements Config {
  @Override
  public void save() {
    JsonObject object = new JsonObject();

    Map<String, Macro> macroIdMap = Nebula.INSTANCE.macro.mapped();
    for (String id : macroIdMap.keySet()) {
      object.add(id, macroIdMap.get(id).save());
    }

    try {
      FileUtils.writeFile(file(), JSONUtils.json(object));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void load() {
    if (!file().exists()) return;

    String content;
    try {
      content = FileUtils.readFile(file());
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    if (content.isEmpty()) return;

    JsonObject object = JSONUtils.parse(content, JsonObject.class);

    Map<String, Macro> macroIdMap = Nebula.INSTANCE.macro.mapped();
    for (String key : macroIdMap.keySet()) {
      JsonElement element = object.get(key);
      if (element == null) continue;

      macroIdMap.get(key).load(element);
    }
  }

  @Override
  public File file() {
    return new File(FileUtils.ROOT, "macros.json");
  }
}
