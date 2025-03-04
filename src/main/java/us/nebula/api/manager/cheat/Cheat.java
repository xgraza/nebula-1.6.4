package us.nebula.api.manager.cheat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import us.nebula.Nebula;
import us.nebula.api.config.IJSONSerializable;
import us.nebula.api.listener.EventBus;
import us.nebula.api.manager.key.Key;
import us.nebula.api.value.ISettingProvider;
import us.nebula.api.value.Setting;

import java.lang.reflect.Field;
import java.util.*;

import static org.lwjgl.input.Keyboard.KEY_NONE;
import static us.nebula.api.manager.key.Key.DEFAULT_UNBOUND_KEY;

/**
 * @author xgraza
 * @since 02/14/25
 */
@SuppressWarnings("unchecked")
public class Cheat implements ISettingProvider, IJSONSerializable
{
    protected static final Minecraft MC = Minecraft.getMinecraft();
    static final String DEFAULT_DESCRIPTION = "No description provided for this cheat";

    private final Map<String, Setting<?>> settingNameMap = new LinkedHashMap<>();
    private final List<Setting<?>> settingList = new LinkedList<>();

    private final CheatManifest manifest;
    private final Key key;

    public Cheat()
    {
        manifest = getClass().getDeclaredAnnotation(CheatManifest.class);
        if (manifest == null)
        {
            throw new RuntimeException(
                    "@CheatManifest needs to be annotated on top of a Cheat class");
        }

        Nebula.INSTANCE.getKeyManager().addKey(manifest.name(),
                key = new Key((state) ->
                {
                    if (state)
                    {
                        onEnable();
                    } else
                    {
                        onDisable();
                    }
                }, false, DEFAULT_UNBOUND_KEY));
    }

    @Override
    public void reflectSettings()
    {
        for (final Field field : getClass().getDeclaredFields())
        {
            if (!Setting.class.isAssignableFrom(field.getType()))
            {
                continue;
            }
            field.setAccessible(true);
            try
            {
                addSetting((Setting<?>) field.get(this));
            } catch (final IllegalAccessException e)
            {
                Nebula.INSTANCE.getLogger().error(
                        "Failed to reflect setting from {}", this);
                Nebula.INSTANCE.getLogger().error(e);
            }
        }
        Nebula.INSTANCE.getLogger().debug("Reflected {} settings from {}",
                settingList.size(), this);
    }

    protected void onEnable()
    {
        EventBus.subscribe(this);
    }

    protected void onDisable()
    {
        EventBus.unsubscribe(this);
    }

    public CheatManifest getManifest()
    {
        return manifest;
    }

    public Key getKey()
    {
        return key;
    }

    public void toggle()
    {
        key.toggle();
    }

    public void setToggled(final boolean toggled)
    {
        key.setState(toggled);
    }

    public boolean isToggled()
    {
        return key.isToggled();
    }

    @Override
    public List<Setting<?>> getSettings()
    {
        return settingList;
    }

    @Override
    public <T> Setting<T> getSetting(final String name)
    {
        return (Setting<T>)settingNameMap.get(name);
    }

    public void addSetting(final Setting<?> setting)
    {
        settingNameMap.put(setting.getName(), setting);
        settingList.add(setting);
    }

    @Override
    public void fromJSON(final JsonElement element)
    {
        if (!element.isJsonObject())
        {
            return;
        }
        final JsonObject object = element.getAsJsonObject();
        setToggled(object.get("toggled").getAsBoolean());
        final JsonObject settingsObj = object.getAsJsonObject("settings");
        for (final String settingName : settingNameMap.keySet())
        {
            if (!settingsObj.has(settingName))
            {
                continue;
            }
            settingNameMap.get(settingName).fromJSON(settingsObj.get(settingName));
        }
    }

    @Override
    public JsonElement toJSON()
    {
        final JsonObject object = new JsonObject();
        object.addProperty("toggled", isToggled());
        final JsonObject settingsObj = new JsonObject();
        for (final Setting<?> setting : getSettings())
        {
            settingsObj.add(setting.getName(), setting.toJSON());
        }
        object.add("settings", settingsObj);
        return object;
    }
}
