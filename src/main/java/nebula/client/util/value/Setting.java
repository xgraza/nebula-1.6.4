package nebula.client.util.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.sentry.Sentry;
import nebula.client.config.JSONSerializable;

import java.awt.*;
import java.util.function.Supplier;

/**
 * @author Gavin
 * @since 08/10/23
 * @param <T>
 */
public class Setting<T> implements JSONSerializable {

  private T value;
  private Number min, max, scale;

  private Supplier<Boolean> visibility = () -> true;
  private SettingMeta meta;

  public Setting(T defaultValue) {
    this.value = defaultValue;
  }

  public Setting(Supplier<Boolean> visibility, T defaultValue) {
    this.visibility = visibility;
    this.value = defaultValue;
  }

  public Setting(Supplier<Boolean> visibility, T defaultValue, Number min, Number max, Number scale) {
    this.visibility = visibility;
    this.value = defaultValue;
    this.min = min;
    this.max = max;
    this.scale = scale;
  }

  public Setting(T defaultValue, Number min, Number max, Number scale) {
    this.value = defaultValue;
    this.min = min;
    this.max = max;
    this.scale = scale;
  }

  public SettingMeta meta() {
    return meta;
  }

  public void setMeta(SettingMeta meta) {
    if (this.meta == null) {
      this.meta = meta;
    }
  }

  public T value() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public void nextEnum() {
    if (value instanceof Enum<?> e) {
      Enum<?>[] constants = e.getDeclaringClass().getEnumConstants();

      int index = e.ordinal() + 1;
      if (index >= constants.length) index = 0;

      setValue((T) constants[index]);
    }
  }

  public void previousEnum() {
    if (value instanceof Enum<?> e) {
      Enum<?>[] constants = e.getDeclaringClass().getEnumConstants();

      int index = e.ordinal() - 1;
      if (index < 0) index = constants.length - 1;

      setValue((T) constants[index]);
    }
  }

  public Number min() {
    return min;
  }

  public void setMin(Number min) {
    this.min = min;
  }

  public Number max() {
    return max;
  }

  public void setMax(Number max) {
    this.max = max;
  }

  public Number scale() {
    return scale;
  }

  public void setScale(Number scale) {
    this.scale = scale;
  }

  public boolean visible() {
    return visibility.get();
  }

  @Override
  public JsonElement save() {
    if (value instanceof Enum<?> e) {
      return new JsonPrimitive(e.ordinal());
    } else if (value instanceof Number n) {
      return new JsonPrimitive(n);
    } else if (value instanceof Color c) {
      JsonObject object = new JsonObject();

      object.addProperty("red", c.getRed());
      object.addProperty("blue", c.getBlue());
      object.addProperty("green", c.getGreen());
      object.addProperty("alpha", c.getAlpha());

      return object;
    } else if (value instanceof Boolean b) {
      return new JsonPrimitive(b);
    }

    return null;
  }

  @Override
  public void load(JsonElement element) {
    if (value instanceof Enum<?> e) {
      int ordinal = element.getAsInt();
      Enum<?>[] constants = e.getDeclaringClass().getEnumConstants();

      try {
        setValue((T) constants[ordinal]);
      } catch (IndexOutOfBoundsException ex) {
        // what a FAGGOT!
        ex.printStackTrace();
        Sentry.captureException(ex);
      }
    } else if (value instanceof Number n) {
      if (n instanceof Float) {
        setValue((T) (Object) element.getAsFloat());
      } else if (n instanceof Double) {
        setValue((T) (Object) element.getAsDouble());
      } else if (n instanceof Integer) {
        setValue((T) (Object) element.getAsInt());
      }
    } else if (value instanceof Color) {
      if (!element.isJsonObject()) return;
      JsonObject object = element.getAsJsonObject();

      setValue((T) new Color(
        object.get("red").getAsInt(),
        object.get("green").getAsInt(),
        object.get("blue").getAsInt(),
        object.get("alpha").getAsInt()));

    } else if (value instanceof Boolean) {
      // kys fag
      setValue((T) (Object) element.getAsBoolean());
    }
  }
}
