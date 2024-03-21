package nebula.client.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.sentry.Sentry;
import nebula.client.Nebula;
import nebula.client.config.JSONSerializable;
import nebula.client.macro.Macro;
import nebula.client.macro.MacroListener;
import nebula.client.macro.MacroType;
import nebula.client.util.render.animation.Animation;
import nebula.client.util.render.animation.Easing;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingContainer;
import nebula.client.util.value.SettingMeta;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Gavin
 * @since 08/09/23
 */
public class Module implements MacroListener, SettingContainer, JSONSerializable {

  /**
   * The minecraft game instance
   */
  protected static final Minecraft mc = Minecraft.getMinecraft();

  private final ModuleMeta meta;
  private final ModuleCategory category;
  private final Macro macro;

  private boolean hidden;

  private final Map<String, Setting<?>> settingNameMap = new LinkedHashMap<>();

  private final Animation animation = new Animation(
    Easing.CUBIC_IN_OUT, 250, false);

  public Module() {
    if (!getClass().isAnnotationPresent(ModuleMeta.class)) {
      RuntimeException e = new RuntimeException("@ModuleMeta needs to be at the top of " + getClass());
      Sentry.captureException(e);
      throw e;
    }

    meta = getClass().getDeclaredAnnotation(ModuleMeta.class);
    macro = new Macro(meta.defaultMacro(), MacroType.KEYBOARD, this);

    category = ModuleCategory.valueOf(getClass().getName()
      .substring(ModuleRegistry.MODULE_IMPL.length() + 1)
      .split("\\.")[0].toUpperCase());
  }

  @Override
  public void enable() {
    Nebula.BUS.subscribe(this);
    animation.setState(true);
  }

  @Override
  public void disable() {
    Nebula.BUS.unsubscribe(this);
    animation.setState(false);
  }

  @Override
  public void init() {
    if (meta.defaultState()) macro.setEnabled(true);
    Nebula.INSTANCE.macro.add(meta.name(), macro);

    for (Field field : getClass().getDeclaredFields()) {
      if (!Setting.class.isAssignableFrom(field.getType())) continue;

      if (field.trySetAccessible()) {

        if (!field.isAnnotationPresent(SettingMeta.class)) {
          RuntimeException e = new RuntimeException(field + " must be annotated with @SettingMeta");
          Sentry.captureException(e);
          throw e;
        }

        try {
          Setting<?> setting = (Setting<?>) field.get(this);
          setting.setMeta(field.getDeclaredAnnotation(SettingMeta.class));

          settingNameMap.put(setting.meta().value(), setting);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
          Sentry.captureException(e);
        }
      }
    }
  }

  @Override
  public Setting<?> get(String name) {
    return settingNameMap.get(name);
  }

  @Override
  public Collection<Setting<?>> settings() {
    return settingNameMap.values();
  }

  public Animation animation() {
    return animation;
  }

  public ModuleMeta meta() {
    return meta;
  }

  public String info() {
    return null;
  }

  public ModuleCategory category() {
    return category;
  }

  public Macro macro() {
    return macro;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public boolean hidden() {
    return hidden;
  }

  @Override
  public JsonElement save() {
    JsonObject object = new JsonObject();

    object.addProperty("hidden", hidden);
    for (Setting<?> setting : settings()) {
      JsonElement settingObj = setting.save();
      if (settingObj == null) continue;
      object.add(setting.meta().value(), settingObj);
    }

    return object;
  }

  @Override
  public void load(JsonElement element) {
    if (!element.isJsonObject()) return;
    JsonObject object = element.getAsJsonObject();

    hidden = object.get("hidden").getAsBoolean();

    for (String name : settingNameMap.keySet()) {
      if (!object.has(name)) continue;
      JsonElement e = object.get(name);
      settingNameMap.get(name).load(e);
    }
  }
}
