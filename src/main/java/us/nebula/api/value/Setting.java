package us.nebula.api.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import us.nebula.api.config.IJSONSerializable;

import java.util.function.Supplier;

/**
 * @author xgraza
 * @since 02/16/25
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class Setting<T> implements IJSONSerializable
{
    private final String name;
    private T value;
    private Number min, max, scale;

    private Supplier<Boolean> visibility = () -> true;
    private ValueChanged<T> valueChanged;

    public Setting(final String name, final T value)
    {
        this.name = name;
        this.value = value;
    }

    public Setting(final String name, final T value, final Supplier<Boolean> visibility)
    {
        this(name, value);
        this.visibility = visibility;
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
        if (valueChanged != null)
        {
            valueChanged.change(this.value, value);
        }
        this.value = value;
    }

    public void nextEnum()
    {
        if (value instanceof Enum<?>)
        {
            final Enum<?> e = (Enum<?>)value;
            final Enum<?>[] constants = e.getDeclaringClass().getEnumConstants();
            int index = e.ordinal() + 1;
            if (index > constants.length - 1)
            {
                index = 0;
            }
            setValue((T)constants[index]);
        }
    }

    public void previousEnum()
    {
        if (value instanceof Enum<?>)
        {
            final Enum<?> e = (Enum<?>)value;
            final Enum<?>[] constants = e.getDeclaringClass().getEnumConstants();
            int index = e.ordinal() - 1;
            if (index < 0)
            {
                index = constants.length - 1;
            }
            setValue((T)constants[index]);
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
        return visibility.get();
    }

    public Setting<T> onValueChange(final ValueChanged<T> valueChanged)
    {
        this.valueChanged = valueChanged;
        return this;
    }

    @Override
    public void fromJSON(final JsonElement element)
    {
        if (!element.isJsonPrimitive())
        {
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
                setValue((T) Enum.valueOf(((Enum<?>)value).getDeclaringClass(), primitive.getAsString()));
            } else
            {
                throw new RuntimeException("mismatched JSON & value types");
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

        if (value instanceof Boolean)
        {
            return new JsonPrimitive((Boolean) value);
        } else if (value instanceof Number)
        {
            return new JsonPrimitive((Number) value);
        } else if (value instanceof Enum<?>)
        {
            return new JsonPrimitive(((Enum<?>)value).name());
        } else
        {
            return new JsonPrimitive(value.toString());
        }
    }
}
