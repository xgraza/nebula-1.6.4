package ez.nebula.client.api.manager.hud2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.Togglable;
import ez.nebula.client.util.io.IJSONSerializable;
import ez.nebula.client.api.manager.hud2.trait.HUDManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.api.setting.SettingProvider;
import ez.nebula.client.impl.gui.hud2.HUDEditorScreen;
import ez.nebula.client.util.render.gui.Render2D;
import ez.nebula.client.util.render.gui.trait.Element;
import ez.nebula.client.util.render.gui.trait.IGUIInputListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author xgraza
 * @since 9/6/2026
 */
public abstract class HUDElement extends Element implements IGUIInputListener, Togglable, SettingProvider, IJSONSerializable
{
    protected static final Minecraft MC = Minecraft.getMinecraft();
    public static final String DEFAULT_DESCRIPTION = "No description provided for this HUD element";

    private final Map<String, Setting<?>> settingNameMap = new HashMap<>();
    private final List<Setting<?>> settingList = new LinkedList<>();

    private final HUDManifest manifest;
    private boolean toggled;

    private double dragX, dragY;
    private boolean dragging;

    public HUDElement()
    {
        if (!getClass().isAnnotationPresent(HUDManifest.class))
        {
            throw new RuntimeException("A HUDElement must be annotated with @HUDManifest");
        }
        manifest = getClass().getDeclaredAnnotation(HUDManifest.class);
        init();
    }

    /**
     * Initializes this {@link HUDElement}
     * @apiNote by default, this takes any default x, y, width, or height values from the @HUDManifest
     */
    public void init()
    {
        if (manifest.defaultX() != -1.0)
        {
            setX(manifest.defaultX());
        }
        if (manifest.defaultY() != -1.0)
        {
            setY(manifest.defaultY());
        }
        if (manifest.defaultWidth() != -1.0)
        {
            setWidth(manifest.defaultWidth());
        }
        if (manifest.defaultHeight() != -1.0)
        {
            setHeight(manifest.defaultHeight());
        }
    }

    /**
     * Called in the HUD editor screen
     * @param mouseX the cursor x position
     * @param mouseY the cursor y position
     */
    public final void render(final int mouseX, final int mouseY)
    {
        render(Render2D.RESOLUTION);
        if (dragging)
        {
            setX(mouseX - dragX);
            setY(mouseY - dragY);
            if (!Mouse.isButtonDown(0) || !(MC.currentScreen instanceof HUDEditorScreen))
            {
                dragging = false;
            }
        }
    }

    /**
     * Renders this {@link HUDElement}
     * @param res the resolution of the game window
     */
    public abstract void render(final ScaledResolution res);

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseIn(mouseX, mouseY) && mouseButton == 0)
        {
            dragging = true;
            dragX = mouseX - getX();
            dragY = mouseY - getY();
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }

    public HUDManifest getManifest()
    {
        return manifest;
    }

    public boolean isDragging()
    {
        return dragging;
    }

    @Override
    public void toggle()
    {
        setToggled(!toggled);
    }

    @Override
    public void setToggled(boolean state)
    {
        toggled = state;
        if (toggled)
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
        return toggled;
    }

    @Override
    public void registerSetting(Setting<?> setting)
    {
        settingNameMap.put(setting.getName(), setting);
        settingList.add(setting);
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

            if (field.isAnnotationPresent(DebugFeature.class) && !Nebula.DEBUG)
            {
                continue;
            }

            field.setAccessible(true);
            try
            {
                registerSetting((Setting<?>) field.get(this));
            } catch (final IllegalAccessException e)
            {
                HUDElementManager.LOGGER.error("Failed to reflect setting", e);
            }
        }
    }

    @Override
    public <T> Setting<T> getSetting(String name)
    {
        return (Setting<T>) settingNameMap.get(name);
    }

    @Override
    public List<Setting<?>> getSettings()
    {
        return settingList;
    }

    @Override
    public JsonElement toJSON()
    {
        final JsonObject object = new JsonObject();
        object.addProperty("x", x);
        object.addProperty("y", y);
        object.addProperty("toggled", toggled);
        final JsonObject settingsObject = new JsonObject();
        for (final Setting<?> setting : settingList)
        {
            settingsObject.add(setting.getName(), setting.toJSON());
        }
        object.add("settings", settingsObject);
        return object;
    }

    @Override
    public void fromJSON(final JsonElement element)
    {
        if (!element.isJsonObject())
        {
            return;
        }
        final JsonObject object = element.getAsJsonObject();

        if (object.has("x") && object.has("y"))
        {
            setX(object.get("x").getAsDouble());
            setY(object.get("y").getAsDouble());
        }

        if (object.has("toggled"))
        {
            setToggled(object.get("toggled").getAsBoolean());
        }

        if (!object.has("settings"))
        {
            return;
        }
        final JsonElement settingsElement = object.get("settings");
        if (!settingsElement.isJsonObject())
        {
            return;
        }
        final JsonObject settingsObject = settingsElement.getAsJsonObject();
        for (final Setting<?> setting : settingList)
        {
            if (!settingsObject.has(setting.getName()))
            {
                continue;
            }
            final JsonElement settingElement = settingsObject.get(setting.getName());
            if (settingElement != null)
            {
                setting.fromJSON(settingElement);
            }
        }
    }
}
