package us.nebula.client.api.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import us.nebula.client.Nebula;
import us.nebula.client.api.config.IJSONSerializable;
import us.nebula.client.api.manager.key.Key;

import java.awt.Color;
import java.io.File;
import java.util.function.Supplier;

/**
 * @param <T>
 * @author xgraza
 * @since 02/16/25
 */
@SuppressWarnings("unchecked")
public class Setting<T> implements IJSONSerializable
{
    private final String name;
    private T value;
    private Number min, max, scale;

    private File baseDirectory;

    private Supplier<Boolean> visibility = () -> true;
    private ValueChanged<T> valueChanged;

    public Setting(final String name, final T value)
    {
        this(name, value, null);
    }

    public Setting(final String name, final T value, final Supplier<Boolean> visibility)
    {
        this.name = name;
        this.value = value;
        this.visibility = visibility;

        if (value instanceof File)
        {
            final File file = (File) value;
            if (!file.exists())
            {
                if (!file.mkdir())
                {
                    throw new RuntimeException("Failed to create directory");
                } else
                {
                    Nebula.INSTANCE.getLogger().info("Created {} successfully.", file);
                }
            }
            this.baseDirectory = file;
            this.value = null;
        }
    }

    public Setting(final String name,
                   final T value,
                   final Number min,
                   final Number max,
                   final Number scale)
    {
        this.name = name;
        if (!(value instanceof Number))
        {
            throw new RuntimeException("'value' must be type of Number");
        }
        this.value = value;
        this.min = min;
        this.max = max;
        this.scale = scale;
    }

    public Setting(final String name,
                   final T value,
                   final Number min,
                   final Number max,
                   final Number scale,
                   final Supplier<Boolean> visibility)
    {
        this(name, value, min, max, scale);
        this.visibility = visibility;
    }

    public String getName()
    {
        return name;
    }

    public T getValue()
    {
        return value;
    }

    public void setValue(T value)
    {
        T oldValue = this.value;
        this.value = value;
        if (valueChanged != null)
        {
            valueChanged.change(oldValue, value);
        }
    }

    public void nextEnum()
    {
        if (value instanceof Enum<?>)
        {
            final Enum<?> e = (Enum<?>) value;
            final Enum<?>[] constants = e.getDeclaringClass().getEnumConstants();
            int index = e.ordinal() + 1;
            if (index > constants.length - 1)
            {
                index = 0;
            }
            setValue((T) constants[index]);
        }
    }

    public void previousEnum()
    {
        if (value instanceof Enum<?>)
        {
            final Enum<?> e = (Enum<?>) value;
            final Enum<?>[] constants = e.getDeclaringClass().getEnumConstants();
            int index = e.ordinal() - 1;
            if (index < 0)
            {
                index = constants.length - 1;
            }
            setValue((T) constants[index]);
        }
    }

    public Number getMin()
    {
        return min;
    }

    public Number getMax()
    {
        return max;
    }

    public Number getScale()
    {
        return scale;
    }

    public Setting<T> setVisibility(final Supplier<Boolean> visibility)
    {
        this.visibility = visibility;
        return this;
    }

    public boolean isVisible()
    {
        return visibility == null || visibility.get();
    }

    public Setting<T> onValueChange(final ValueChanged<T> valueChanged)
    {
        this.valueChanged = valueChanged;
        return this;
    }

    public File getBaseDirectory()
    {
        return baseDirectory;
    }

    @Override
    public void fromJSON(final JsonElement element)
    {
        if (!element.isJsonPrimitive())
        {
            if (element.isJsonObject())
            {
                final JsonObject object = element.getAsJsonObject();
                if (value instanceof Key)
                {
                    ((Key) value).fromJSON(object);
                } else if (value instanceof Color)
                {
                    int r = 255, g = 255, b = 255, a = 255;
                    if (object.has("r"))
                    {
                        r = object.get("r").getAsInt();
                    }
                    if (object.has("g"))
                    {
                        g = object.get("g").getAsInt();
                    }
                    if (object.has("b"))
                    {
                        b = object.get("b").getAsInt();
                    }
                    if (object.has("a"))
                    {
                        a = object.get("a").getAsInt();
                    }
                    setValue((T) new Color(r, g, b, a));
                }
            }
            return;
        }
        final JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (primitive.isBoolean())
        {
            if (!(value instanceof Boolean))
            {
                throw new RuntimeException("mismatched JSON & value types");
            }
            setValue((T) (Object) primitive.getAsBoolean());
        } else if (primitive.isNumber())
        {
            if (!(value instanceof Number))
            {
                throw new RuntimeException("mismatched JSON & value types");
            }
            if (value instanceof Integer)
            {
                setValue((T) (Object) primitive.getAsInt());
            } else if (value instanceof Float)
            {
                setValue((T) (Object) primitive.getAsFloat());
            } else if (value instanceof Double)
            {
                setValue((T) (Object) primitive.getAsDouble());
            }
        } else if (primitive.isString())
        {
            if (value instanceof Enum<?>)
            {
                setValue((T) Enum.valueOf(((Enum<?>) value).getDeclaringClass(), primitive.getAsString()));
            } else if (value instanceof File || baseDirectory != null)
            {
                final File file = new File(primitive.getAsString());
                if (!file.isDirectory() && file.exists() && file.canRead())
                {
                    setValue((T) file);
                } else
                {
                    Nebula.INSTANCE.getLogger().warn(
                            "{} does not exist/doesnt have rw privileges",
                            file);
                }
            } else
            {
                throw new RuntimeException("mismatched JSON & value types for setting "
                        + getName());
            }
        }
    }

    @Override
    public JsonElement toJSON()
    {
        if (value == null)
        {
            return JsonNull.INSTANCE;
        }

        if (value instanceof File)
        {
            return new JsonPrimitive(((File) value).getAbsolutePath());
        } else if (value instanceof Key)
        {
            return ((Key) value).toJSON();
        } else if (value instanceof Color)
        {
            final Color c = (Color) value;
            final JsonObject object = new JsonObject();
            object.addProperty("r", c.getRed());
            object.addProperty("g", c.getGreen());
            object.addProperty("b", c.getBlue());
            object.addProperty("a", c.getAlpha());
            return object;
        } else if (value instanceof Boolean)
        {
            return new JsonPrimitive((Boolean) value);
        } else if (value instanceof Number)
        {
            return new JsonPrimitive((Number) value);
        } else if (value instanceof Enum<?>)
        {
            return new JsonPrimitive(((Enum<?>) value).name());
        } else
        {
            return new JsonPrimitive(value.toString());
        }
    }
}
