package ez.nebula.client.api.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import ez.nebula.client.api.config.IJSONSerializable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Setting<T> implements IJSONSerializable
{
    private final String name, description;
    private final T defaultValue;
    private final Predicate<T> visibility;
    private final Consumer<T> valueChanged;
    private final Class<T> type;
    private T value;

    @SuppressWarnings("unchecked")
    public Setting(final String name, final String description, final Predicate<T> visibility, final Consumer<T> valueChanged, final T value)
    {
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.valueChanged = valueChanged;
        setValue(value);
        this.defaultValue = value;
        this.type = (Class<T>) value.getClass();
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public T getDefaultValue()
    {
        return defaultValue;
    }

    public Class<T> getType()
    {
        return type;
    }

    public void setValue(T value)
    {
        this.value = value;
        if (valueChanged != null)
        {
            valueChanged.accept(value);
        }
    }

    public T getValue()
    {
        return value;
    }

    public boolean isVisible()
    {
        return visibility == null || visibility.test(value);
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
        }

//        else if (primitive.isString())
//        {
//            if (value instanceof Enum<?>)
//            {
//                setValue((T) Enum.valueOf(((Enum<?>) value).getDeclaringClass(), primitive.getAsString()));
//            } else if (value instanceof File || baseDirectory != null)
//            {
//                final File file = new File(primitive.getAsString());
//                if (!file.isDirectory() && file.exists() && file.canRead())
//                {
//                    setValue((T) file);
//                } else
//                {
//                    Nebula.INSTANCE.getLogger().warn(
//                            "{} does not exist/doesnt have rw privileges",
//                            file);
//                }
//            } else
//            {
//                throw new RuntimeException("mismatched JSON & value types for setting "
//                        + getName());
//            }
//        }
    }

    @Override
    public JsonElement toJSON()
    {
        if (value == null)
        {
            return JsonNull.INSTANCE;
        }

        if (String.class.isAssignableFrom(type))
        {
            return new JsonPrimitive((String) getValue());
        } else if (Boolean.class.isAssignableFrom(type))
        {
            return new JsonPrimitive((Boolean) getValue());
        } else if (Number.class.isAssignableFrom(type))
        {
            return new JsonPrimitive((Number) getValue());
        } else
        {
            return JsonNull.INSTANCE;
        }
    }

    public static class Builder<T>
    {
        protected final String name;
        protected T value;
        protected String description = "No description was provided for this setting";
        protected Predicate<T> visibility;
        protected Consumer<T> valueChanged;

        public Builder(String name, T value)
        {
            this.name = name;
            this.value = value;
        }

        protected void setValue(T value)
        {
            this.value = value;
        }

        public Builder<T> setDescription(String description)
        {
            this.description = description;
            return this;
        }

        public Builder<T> setVisibility(final Predicate<T> visibility)
        {
            this.visibility = visibility;
            return this;
        }

        public Builder<T> onValueChanged(final Consumer<T> valueChanged)
        {
            this.valueChanged = valueChanged;
            return this;
        }

        public Setting<T> build()
        {
            return new Setting<>(name, description, visibility, valueChanged, value);
        }
    }
}
