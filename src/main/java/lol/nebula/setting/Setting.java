package lol.nebula.setting;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lol.nebula.util.feature.ITaggable;

import java.awt.*;
import java.util.function.Supplier;

/**
 * @author aesthetical
 * @since 04/27/23
 * @param <T> the setting type
 */
public class Setting<T> implements ITaggable, IJsonSerializable {
    private T value;
    private final String tag;

    private final Number min, max, scale;

    private Supplier<Boolean> visibility;

    public Setting(T value, String tag) {
        this(null, value, null, null, null, tag);
    }

    public Setting(Supplier<Boolean> visibility, T value, String tag) {
        this(visibility, value, null, null, tag);
    }

    public Setting(T value, Number min, Number max, String tag) {
        this(null, value, null, min, max, tag);
    }

    public Setting(T value, Number scale, Number min, Number max, String tag) {
        this(null, value, scale, min, max, tag);
    }

    public Setting(Supplier<Boolean> visibility, T value, Number min, Number max, String tag) {
        this(visibility, value, null, min, max, tag);
    }

    public Setting(Supplier<Boolean> visibility, T value, Number scale, Number min, Number max, String tag) {
        this.visibility = visibility;
        this.tag = tag;
        this.value = value;
        this.scale = scale == null ? 1.0 : scale;
        this.min = min;
        this.max = max;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void next() {
        if (value instanceof Enum<?>) {
            Enum<?> e = (Enum<?>) value;
            Enum<?>[] constants = e.getDeclaringClass().getEnumConstants();
            int i = e.ordinal() + 1;

            if (i >= constants.length) i = 0;

            value = (T) constants[i];
        }
    }

    public void previous() {
        if (value instanceof Enum<?>) {
            Enum<?> e = (Enum<?>) value;
            Enum<?>[] constants = e.getDeclaringClass().getEnumConstants();
            int i = e.ordinal() - 1;

            if (i < 0) i = constants.length - 1;

            value = (T) constants[i];
        }
    }

    public Number getMax() {
        return max;
    }

    public Number getMin() {
        return min;
    }

    public Number getScale() {
        return scale;
    }

    public Setting<T> setVisibility(Supplier<Boolean> visibility) {
        this.visibility = visibility;
        return this;
    }

    public boolean isVisible() {
        return visibility == null || visibility.get();
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void fromJson(JsonObject object) {

    }

    @Override
    public JsonObject toJson() {
        JsonObject settingObject = new JsonObject();
        settingObject.addProperty("id", tag);

        if (value instanceof Color) {
            JsonObject colorObject = new JsonObject();
            Color color = (Color) value;
            colorObject.addProperty("r", color.getRed());
            colorObject.addProperty("g", color.getGreen());
            colorObject.addProperty("b", color.getBlue());
            colorObject.addProperty("a", color.getAlpha());
        } else if (value instanceof Number) {
            if (value instanceof Double) {
                settingObject.addProperty("value", ((Double) value));
            } else if (value instanceof Float) {
                settingObject.addProperty("value", ((Float) value));
            } else if (value instanceof Integer) {
                settingObject.addProperty("value", ((Integer) value));
            }
        } else if (value instanceof Boolean) {
            settingObject.addProperty("value", ((Boolean) value));
        }

        return settingObject;
    }
}
