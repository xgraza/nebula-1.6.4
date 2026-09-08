package ez.nebula.client.api.manager.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.Nebula;
import net.minecraft.client.Minecraft;
import ez.nebula.client.ClientConfig;
import ez.nebula.client.util.io.IJSONSerializable;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.manager.key.Key;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.api.setting.SettingProvider;
import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.Togglable;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static ez.nebula.client.api.manager.key.Key.DEFAULT_UNBOUND_KEY;

/**
 * @author xgraza
 * @since 02/14/25
 */
@SuppressWarnings("unchecked")
public class Module implements SettingProvider, IJSONSerializable, Togglable
{
    protected static final Minecraft MC = Minecraft.getMinecraft();
    public static final String DEFAULT_DESCRIPTION = "No description provided for this module";

    private final Map<String, Setting<?>> settingNameMap = new LinkedHashMap<>();
    private final List<Setting<?>> settingList = new LinkedList<>();

    private final ModuleManifest manifest;
    private final Key key;
    private final boolean debug;

    /**
     * If this module should be hidden from the Arraylist render
     */
    private final Setting<Boolean> hiddenSetting = builder("Hidden", false)
            .setDescription("If to hide this module from the arraylist")
            .build();

    public Module()
    {
        manifest = getClass().getDeclaredAnnotation(ModuleManifest.class);
        if (manifest == null)
        {
            throw new RuntimeException(
                    "@ModuleManifest needs to be annotated on top of a Module class");
        }
        debug = getClass().isAnnotationPresent(DebugFeature.class);

        Nebula.KEYS.register(manifest.name(),
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
        Nebula.TOASTS.info(manifest.name(), message, duration);
    }

    public void notifyWarn(final String message, final long duration)
    {
        Nebula.TOASTS.warn(manifest.name(), message, duration);
    }

    public void notifyError(final String message, final long duration)
    {
        Nebula.TOASTS.error(manifest.name(), message, duration);
    }

    public ModuleManifest getManifest()
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
        hiddenSetting.setValue(hidden);
    }

    public boolean isHidden()
    {
        return hiddenSetting.getValue();
    }

    public boolean isDebug()
    {
        return debug;
    }

    @Override
    public void discoverSettings()
    {
        registerSetting(hiddenSetting);
        for (final Field field : getClass().getDeclaredFields())
        {
            if (!Setting.class.isAssignableFrom(field.getType()))
            {
                continue;
            }

            if (field.isAnnotationPresent(DebugFeature.class) && !ClientConfig.DEBUG)
            {
                continue;
            }

            field.setAccessible(true);
            try
            {
                registerSetting((Setting<?>) field.get(this));
            } catch (final IllegalAccessException e)
            {
                ModuleManager.LOGGER.error("Failed to reflect setting", e);
            }
        }
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
    public void registerSetting(final Setting<?> setting)
    {
        settingNameMap.put(setting.getName(), setting);
        settingList.add(setting);

        if (setting.getValue() instanceof Key)
        {
            ModuleManager.LOGGER.debug("Added runtime key for setting {}", setting);
            Nebula.KEYS.registerRuntime((Key) setting.getValue());
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
