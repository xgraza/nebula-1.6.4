package us.nebula.client.api.manager.hud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import us.nebula.client.api.config.IJSONSerializable;
import us.nebula.client.api.gui.GUIComponent;
import us.nebula.client.api.trait.Togglable;
import us.nebula.client.api.value.ISettingProvider;
import us.nebula.client.api.value.Setting;
import us.nebula.client.util.render.RenderUtil;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author xgraza
 * @since 3/23/26
 */
public class HUDElement extends GUIComponent implements ISettingProvider, IJSONSerializable, Togglable
{
    protected static final Minecraft MC = Minecraft.getMinecraft();
    static final String DEFAULT_DESCRIPTION = "No description provided for this element";

    private final Map<String, Setting<?>> settingNameMap = new LinkedHashMap<>();
    private final List<Setting<?>> settingList = new LinkedList<>();

    private final HUDManifest manifest;
    private final double padding;
    private Setting<Boolean> toggledSetting;
    private boolean initialized;

    public HUDElement()
    {
        manifest = getClass().getDeclaredAnnotation(HUDManifest.class);
        if (manifest == null)
        {
            throw new RuntimeException(
                    "@HUDManifest needs to be annotated on top of a HUDElement class");
        }

        if (manifest.x() != -1)
        {
            setX(manifest.x());
        }

        if (manifest.y() != -1)
        {
            setY(manifest.y());
        }

        if (manifest.height() != -1)
        {
            setHeight(manifest.height());
        }

        if (manifest.width() != -1)
        {
            setWidth(manifest.width());
        }

        padding = manifest.padding();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        render(RenderUtil.GAME_RESOLUTION);
    }

    public void render(final ScaledResolution res)
    {
        if (!initialized)
        {
            init();
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
