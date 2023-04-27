package lol.nebula.setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class SettingContainer implements IJsonSerializable {

    private final Map<String, Setting<?>> settingNameMap = new LinkedHashMap<>();

    @Override
    public void fromJson(JsonObject object) {

    }

    @Override
    public JsonObject toJson() {
        JsonObject configObject = new JsonObject();

        JsonArray settingArray = new JsonArray();
        settingNameMap.forEach((name, setting) -> settingArray.add(setting.toJson()));

        configObject.add("settings", settingArray);

        return configObject;
    }

    /**
     * Registers a setting to this container
     * @param setting the setting
     */
    public void register(Setting<?> setting) {
        settingNameMap.put(setting.getTag(), setting);
    }

    /**
     * Gets a setting based off the name
     * @param name the setting name
     * @return the setting that is registered to that name, or null
     * @param <T> the setting value type
     */
    public <T> Setting<T> getSetting(String name) {
        return (Setting<T>) settingNameMap.getOrDefault(name, null);
    }

    /**
     * Gets all the setting values
     * @return the setting values
     */
    public Collection<Setting<?>> getSettings() {
        return settingNameMap.values();
    }
}
