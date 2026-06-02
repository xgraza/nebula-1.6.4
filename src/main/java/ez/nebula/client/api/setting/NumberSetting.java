package ez.nebula.client.api.setting;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @param <T> the type of number
 * @author xgraza
 * @since 1/27/26
 */
public final class NumberSetting<T extends Number> extends Setting<T>
{
    private final T min, max, scale;

    public NumberSetting(String name, String description, Predicate<T> visibility, Consumer<T> valueChanged, T value, T min, T max, T scale)
    {
        super(name, description, visibility, valueChanged, value);
        this.min = min;
        this.max = max;
        this.scale = scale;
    }

    public T getMin()
    {
        return min;
    }

    public T getMax()
    {
        return max;
    }

    public T getScale()
    {
        return scale;
    }

    public static final class Builder<T extends Number> extends Setting.Builder<T>
    {
        private T min, max, scale;

        public Builder(String name, T value)
        {
            super(name, value);
        }

        public Builder<T> setMin(T min)
        {
            this.min = min;
            return this;
        }

        public Builder<T> setMax(T max)
        {
            this.max = max;
            return this;
        }

        public Builder<T> setScale(T scale)
        {
            this.scale = scale;
            return this;
        }

        @Override
        public NumberSetting<T> build()
        {
            Objects.requireNonNull(min, "must set a min value");
            Objects.requireNonNull(max, "must set a max value");
            Objects.requireNonNull(scale, "must set a scale value");
            return new NumberSetting<T>(name, description, visibility, valueChanged, value, min, max, scale);
        }
    }
}