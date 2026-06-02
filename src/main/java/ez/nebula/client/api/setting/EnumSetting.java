package ez.nebula.client.api.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @param <T> the type of enum
 * @author xgraza
 * @since 1/27/26
 */
public final class EnumSetting<T extends Enum<T>> extends Setting<T>
{
    private final T[] enumConstants;

    public EnumSetting(String name, String description, Predicate<T> visibility, Consumer<T> valueChanged, T value)
    {
        super(name, description, visibility, valueChanged, value);
        enumConstants = value.getDeclaringClass().getEnumConstants();
    }

    @Override
    public void setValue(final T value)
    {
        super.setValue(value);
    }

    public void nextValue()
    {
        int nextOrdinal = getValue().ordinal() + 1;
        if (nextOrdinal > enumConstants.length - 1)
        {
            nextOrdinal = 0;
        }
        setValue(getForOrdinal(nextOrdinal));
    }

    public void previousValue()
    {
        int nextOrdinal = getValue().ordinal() - 1;
        if (nextOrdinal < 0)
        {
            nextOrdinal = enumConstants.length - 1;
        }
        setValue(getForOrdinal(nextOrdinal));
    }

    public T getForOrdinal(final int ordinal)
    {
        if (ordinal > enumConstants.length - 1 || ordinal < 0)
        {
            return null;
        }
        return enumConstants[ordinal];
    }

    @Override
    public void fromJSON(final JsonElement element)
    {
        if (!element.isJsonPrimitive())
        {
            return;
        }
        final JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (primitive.isNumber())
        {
            final T value = getForOrdinal(primitive.getAsInt());
            if (value == null)
            {
                return;
            }
            setValue(value);
        }
    }

    @Override
    public JsonElement toJSON()
    {
        return new JsonPrimitive(getValue().ordinal());
    }

    public static final class Builder<T extends Enum<T>> extends Setting.Builder<T>
    {
        public Builder(String name, T value)
        {
            super(name, value);
        }

        @Override
        public EnumSetting<T> build()
        {
            return new EnumSetting<>(name, description, visibility, valueChanged, value);
        }
    }
}