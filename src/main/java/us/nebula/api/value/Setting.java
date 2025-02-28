package us.nebula.api.value;

import java.util.function.Supplier;

/**
 * @author xgraza
 * @since 02/16/25
 * @param <T>
 */
@SuppressWarnings("unchecked")
public final class Setting<T>
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

    public boolean isVisible()
    {
        return visibility.get();
    }

    public Setting<T> onValueChange(final ValueChanged<T> valueChanged)
    {
        this.valueChanged = valueChanged;
        return this;
    }
}
