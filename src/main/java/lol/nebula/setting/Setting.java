package lol.nebula.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.nebula.bind.Bind;
import lol.nebula.bind.BindDevice;
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
        if (object.has("value")) {
            JsonElement element = object.get("value");

            if (value instanceof Color) {
                if (!element.isJsonObject()) return;

                JsonObject colorObject = element.getAsJsonObject();

                value = (T) new Color(
                        colorObject.get("r").getAsInt(),
                        colorObject.get("g").getAsInt(),
                        colorObject.get("b").getAsInt(),
                        colorObject.get("a").getAsInt());
            } else if (value instanceof Number) {
                if (value instanceof Double) {
                    value = (T) (Double) element.getAsDouble();
                } else if (value instanceof Float) {
                    value = (T) (Float) element.getAsFloat();
                } else if (value instanceof Integer) {
                    value = (T) (Integer) element.getAsInt();
                }
            } else if (value instanceof Boolean) {
                value = (T) (Boolean) element.getAsBoolean();
            } else if (value instanceof Enum<?>) {
                try {
                    value = (T) (Enum<?>) Enum.valueOf((Class<Enum>) value.getClass(), element.getAsString());
                } catch (Exception ignored) {

                }
            }
        }
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
        } else if (value instanceof Enum<?>) {
            settingObject.addProperty("value", ((Enum<?>) value).name());
        }

        return settingObject;
    }

    public static String formatEnumName(Enum<?> input) {
        String name = input.toString();

        // allows custom overrides for names that may not be possible in java
        if (!name.equals(input.name())) {
            return name;
        }

        String[] parts = name.split("_");

        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            builder.append(Character.toString(part.charAt(0)).toUpperCase())
                    .append(part.substring(1).toLowerCase())
                    .append(" ");
        }

        return builder.toString().trim();
    }
}
