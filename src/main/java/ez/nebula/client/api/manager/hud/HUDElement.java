package ez.nebula.client.api.manager.hud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.api.manager.hud.trait.HUDManifest;
import ez.nebula.client.util.render.gui.Render2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import ez.nebula.client.core.ClientConfig;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.config.IJSONSerializable;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.api.setting.SettingProvider;
import ez.nebula.client.util.render.gui.trait.GUIComponent;
import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.Togglable;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author xgraza
 * @since 3/23/26
 */
public class HUDElement extends GUIComponent implements SettingProvider, IJSONSerializable, Togglable
{
    protected static final Minecraft MC = Minecraft.getMinecraft();
    public static final String DEFAULT_DESCRIPTION = "No description provided for this element";

    private final Map<String, Setting<?>> settingNameMap = new LinkedHashMap<>();
    private final List<Setting<?>> settingList = new LinkedList<>();

    private final HUDManifest manifest;
    private final double padding;
    private Setting<Boolean> toggledSetting;

    public HUDElement()
    {
        manifest = getClass().getDeclaredAnnotation(HUDManifest.class);
        if (manifest == null)
        {
            throw new RuntimeException(
                    "@HUDManifest needs to be annotated on top of a HUDElement class");
        }

        setX(manifest.x());
        setY(manifest.y());
        setHeight(manifest.height());
        setWidth(manifest.width());
        padding = manifest.padding();
    }

    @Override
    public void onEnable()
    {
        EventBus.subscribe(this);
    }

    @Override
    public void onDisable()
    {
        EventBus.unsubscribe(this);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        render(Render2D.RESOLUTION);
    }

    public void render(final ScaledResolution res)
    {

    }

    @Override
    public void discoverSettings()
    {
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
                Nebula.INSTANCE.getLogger().error(
                        "Failed to reflect setting from {}", this);
                Nebula.INSTANCE.getLogger().error(e);
            }
        }
    }

    @Override
    public List<Setting<?>> getSettings()
    {
        return settingList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Setting<T> getSetting(final String name)
    {
        return (Setting<T>) settingNameMap.get(name);
    }

    @Override
    public void registerSetting(final Setting<?> setting)
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
        if (object.has("toggled"))
        {
            setToggled(object.get("toggled").getAsBoolean());
        }
        if (object.has("x"))
        {
            setX(object.get("x").getAsDouble());
        }
        if (object.has("y"))
        {
            setY(object.get("y").getAsDouble());
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
        object.addProperty("x", getX());
        object.addProperty("y", getY());
        final JsonObject settingsObj = new JsonObject();
        for (final Setting<?> setting : getSettings())
        {
            settingsObj.add(setting.getName(), setting.toJSON());
        }
        object.add("settings", settingsObj);
        return object;
    }

    @Override
    public void toggle()
    {
        setToggled(!isToggled());
    }

    @Override
    public void setToggled(boolean state)
    {
        if (toggledSetting.getValue() != state)
        {
            toggledSetting.setValue(state);
        }
        if (state)
        {
            onEnable();
        } else
        {
            onDisable();
        }
    }

    @Override
    public boolean isToggled()
    {
        return toggledSetting.getValue();
    }

    public HUDManifest getManifest()
    {
        return manifest;
    }

    public double getPadding()
    {
        return padding;
    }

    public void setToggledSetting(Setting<Boolean> toggledSetting)
    {
        this.toggledSetting = toggledSetting;
    }
}
