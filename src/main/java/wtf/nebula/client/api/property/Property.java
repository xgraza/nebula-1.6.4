package wtf.nebula.client.api.property;

import wtf.nebula.client.utils.client.Labeled;

import java.util.function.Supplier;

public class Property<T> implements Labeled {
    private T value;
    private final Number min, max;

    private final String label;
    private final String[] aliases;

    private Supplier<Boolean> visibility;

    public Property(T value, String... aliases) {
        this(value, null, null, aliases);
    }

    public Property(T value, Number min, Number max, String... aliases) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.label = aliases[0];
        this.aliases = aliases;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;

        if (value instanceof Number) {
            if (max.floatValue() < ((Number) value).floatValue()) {
                this.value = (T) max;
            }

            if (min.floatValue() > ((Number) value).floatValue()) {
                this.value = (T) min;
            }
        }
    }

    public Number getMin() {
        return min;
    }

    public Number getMax() {
        return max;
    }

    public Property<T> setVisibility(Supplier<Boolean> visibility) {
        this.visibility = visibility;
        return this;
    }

    public boolean isVisible() {
        return visibility == null || visibility.get();
    }

    public void next() {
        if (value instanceof Enum) {
            Enum<?> val = ((Enum<?>) value);
            Enum<?>[] constants = val.getDeclaringClass().getEnumConstants();

            int id = val.ordinal() + 1;
            if (constants.length - 1 < id) {
                id = 0;
            }

            value = (T) constants[id];
        }
    }

    public void previous() {
        if (value instanceof Enum) {
            Enum<?> val = ((Enum<?>) value);
            Enum<?>[] constants = val.getDeclaringClass().getEnumConstants();

            int id = val.ordinal() - 1;

            if (id < 0) {
                id = constants.length - 1;
            }

            value = (T) constants[id];
        }
    }

    public String getFixedValue() {
        if (value instanceof Enum) {
            return formatEnum((Enum) value);
        } else {
            return getLabel();
        }
    }

    public static String formatEnum(Enum value) {
        String name = ((Enum<?>) value).toString();
        return Character.toString(name.charAt(0)).toUpperCase() + name.substring(1).toLowerCase();
    }
}
