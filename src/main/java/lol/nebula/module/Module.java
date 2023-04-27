package lol.nebula.module;

import lol.nebula.Nebula;
import lol.nebula.bind.Bind;
import lol.nebula.bind.BindDevice;
import lol.nebula.setting.Setting;
import lol.nebula.setting.SettingContainer;
import lol.nebula.util.feature.ITaggable;
import lol.nebula.util.feature.IToggleable;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class Module extends SettingContainer implements ITaggable, IToggleable {

    /**
     * The {@link Minecraft} instance used in this module
     */
    protected final Minecraft mc = Minecraft.getMinecraft();

    private final String tag, description;
    private final ModuleCategory category;

    private final Setting<Bind> bind = new Setting<>(new Bind(
            (bind) -> setState(bind.isToggled()), BindDevice.KEYBOARD, KEY_NONE), "Bind");

    /**
     * The module state
     */
    private boolean state;

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
    }

    @Override
    public void onDisable() {
        Nebula.getBus().unsubscribe(this);
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

    @Override
    public boolean isToggled() {
        return state;
    }

    @Override
    public void setState(boolean state) {
        this.state = state;
        if (state) {
            onEnable();
        } else {
            onDisable();
        }
    }
}
