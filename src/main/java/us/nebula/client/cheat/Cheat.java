package us.nebula.client.cheat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import us.nebula.client.Nebula;
import us.nebula.client.config.IJSONSerializable;
import us.nebula.client.listener.EventBus;
import us.nebula.client.key.Key;
import us.nebula.client.util.trait.Togglable;
import us.nebula.client.util.value.ISettingProvider;
import us.nebula.client.util.value.Setting;
import us.nebula.client.cheat.trait.CheatManifest;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static us.nebula.client.key.Key.DEFAULT_UNBOUND_KEY;

/**
 * @author xgraza
 * @since 02/14/25
 */
@SuppressWarnings("unchecked")
public class Cheat implements ISettingProvider, IJSONSerializable, Togglable
{
    protected static final Minecraft MC = Minecraft.getMinecraft();
    public static final String DEFAULT_DESCRIPTION = "No description provided for this cheat";

    private final Map<String, Setting<?>> settingNameMap = new LinkedHashMap<>();
    private final List<Setting<?>> settingList = new LinkedList<>();

    private final CheatManifest manifest;
    private final Key key;

    /**
     * If this cheat should be hidden from the Arraylist render
     */
    private boolean hidden;

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

    public void onEnable()
    {
        EventBus.subscribe(this);
    }

    public void onDisable()
    {
        EventBus.unsubscribe(this);
    }

    public void notifyInfo(final String message, final long duration)
    {
        Nebula.INSTANCE.getToastManager().info(manifest.name(), message, duration);
    }

    public void notifyWarn(final String message, final long duration)
    {
        Nebula.INSTANCE.getToastManager().warn(manifest.name(), message, duration);
    }

    public void notifyError(final String message, final long duration)
    {
        Nebula.INSTANCE.getToastManager().error(manifest.name(), message, duration);
    }

    public CheatManifest getManifest()
    {
        return manifest;
    }

    public String getMetadata()
    {
        return null;
    }

    public Key getKey()
    {
        return key;
    }

    @Override
    public void toggle()
    {
        key.toggle();
    }

    @Override
    public void setToggled(final boolean toggled)
    {
        key.setState(toggled);
    }

    @Override
    public boolean isToggled()
    {
        return key.isToggled();
    }

    public boolean isActive()
    {
        return isToggled();
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    @Override
    public List<Setting<?>> getSettings()
    {
        return settingList;
    }

    @Override
    public <T> Setting<T> getSetting(final String name)
    {
        return (Setting<T>) settingNameMap.get(name);
    }

    @Override
    public void addSetting(final Setting<?> setting)
    {
        settingNameMap.put(setting.getName(), setting);
        settingList.add(setting);

        if (setting.getValue() instanceof Key)
        {
            Nebula.INSTANCE.getLogger().debug(
                    "Added runtime key for setting {}", setting);
            Nebula.INSTANCE.getKeyManager()
                    .addRuntimeKey((Key) setting.getValue());
        }
    }

    @Override
    public void fromJSON(final JsonElement element)
    {
        if (!element.isJsonObject())
        {
            return;
        }
        final JsonObject object = element.getAsJsonObject();
        if (object.has("toggled"))
        {
            setToggled(object.get("toggled").getAsBoolean());
        }
        if (object.has("hidden"))
        {
            setHidden(object.get("hidden").getAsBoolean());
        }
        if (!object.has("settings"))
        {
            return;
        }
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
        object.addProperty("hidden", isHidden());
        final JsonObject settingsObj = new JsonObject();
        for (final Setting<?> setting : getSettings())
        {
            settingsObj.add(setting.getName(), setting.toJSON());
        }
        object.add("settings", settingsObj);
        return object;
    }
}
