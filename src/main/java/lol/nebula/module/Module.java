package lol.nebula.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.nebula.Nebula;
import lol.nebula.bind.Bind;
import lol.nebula.bind.BindDevice;
import lol.nebula.setting.IJsonSerializable;
import lol.nebula.setting.Setting;
import lol.nebula.setting.SettingContainer;
import lol.nebula.util.feature.ITaggable;
import lol.nebula.util.feature.IToggleable;
import lol.nebula.util.render.animation.Animation;
import lol.nebula.util.render.animation.Easing;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class Module extends SettingContainer implements ITaggable, IToggleable, IJsonSerializable {

    /**
     * The {@link Minecraft} instance used in this module
     */
    protected final Minecraft mc = Minecraft.getMinecraft();

    private final String tag, description;
    private final ModuleCategory category;

    private final Animation animation = new Animation(Easing.CUBIC_IN_OUT, 235, false);

    private final Setting<Bind> bind = new Setting<>(new Bind(
            (bind) -> setState(bind.isToggled()), BindDevice.KEYBOARD, KEY_NONE), "Bind");

    /**
     * The module state
     */
    private boolean state;

    /**
     * If the module should be drawn to the arraylist
     */
    private boolean drawn = true;

    /**
     * Creates a new module object
     * @param tag the module tag
     * @param description the description of this module
     * @param category the category this module belongs to
     */
    public Module(String tag, String description, ModuleCategory category) {
        this.tag = tag;
        this.description = description;
        this.category = category;
    }

    @Override
    public void onEnable() {
        Nebula.getBus().subscribe(this);
        animation.setState(true);
    }

    @Override
    public void onDisable() {
        Nebula.getBus().unsubscribe(this);
        animation.setState(false);
    }

    /**
     * Loads settings via reflection
     */
    public void loadSettings() {
        for (Field field : getClass().getDeclaredFields()) {
            if (Setting.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);

                try {
                    Setting<?> setting = (Setting<?>) field.get(this);
                    if (setting != null) {
                        register(setting);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        // register static settings
        register(bind);
        // register bind to bind manager
        Nebula.getInstance().getBinds().addBind(bind.getValue());
    }

    @Override
    public String getTag() {
        return tag;
    }

    public String getDescription() {
        return description;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    /**
     * Gets this module's key bind
     * @return the bind value from this module's bind setting
     */
    public Bind getBind() {
        return bind.getValue();
    }

    /**
     * Gets this module's animation
     * @return the module animation instance
     */
    public Animation getAnimation() {
        return animation;
    }

    @Override
    public boolean isToggled() {
        return state;
    }

    @Override
    public void setState(boolean state) {
        this.state = state;
        getBind().setValue(state);
        if (state) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public boolean isDrawn() {
        return drawn;
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    @Override
    public void fromJson(JsonObject object) {
        if (object.has("settings")) {
            JsonElement element = object.get("settings");
            if (!element.isJsonArray()) return;

            for (JsonElement e : object.get("settings").getAsJsonArray()) {
                if (e.isJsonObject()) {
                    JsonObject settingObject = e.getAsJsonObject();
                    Setting<?> setting = getSetting(settingObject.get("id").getAsString());
                    if (setting != null) setting.fromJson(settingObject);
                }
            }
        }

        if (object.has("drawn")) {
            drawn = object.get("drawn").getAsBoolean();
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject parent = super.toJson();
        parent.addProperty("tag", tag);
        parent.addProperty("drawn", drawn);
        return parent;
    }
}
