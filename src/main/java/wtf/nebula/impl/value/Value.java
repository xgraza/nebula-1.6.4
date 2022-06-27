package wtf.nebula.impl.value;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Represents a mutable value in a feature
 * @param <T> The type the value will be
 *
 * @author aesthetical
 * @since 03/07/22
 */
@SuppressWarnings("rawtypes")
public class Value<T> {
    private final String name;
    private T value;

    private final Number min, max;

    private final Value parent;
    private final Supplier<Boolean> visibility;

    public final ArrayList<Value<?>> children = new ArrayList<>();

    public Value(String name, T value) {
        this(null, name, value);
    }

    public Value(Value Value, String name, T value) {
        this(Value, name, value, null, null, null);
    }

    public Value(Value Value, String name, T value, Supplier<Boolean> visibility) {
        this(Value, name, value, null, null, visibility);
    }

    public Value(String name, T value, Number min, Number max) {
        this(name, value, min, max, null);
    }

    public Value(String name, T value, Number min, Number max, Supplier<Boolean> visibility) {
        this(null, name, value, min, max, visibility);
    }

    public Value(Value parent, String name, T value, Number min, Number max) {
        this(parent, name, value, min, max, null);
    }

    public Value(Value parent, String name, T value, Number min, Number max, Supplier<Boolean> visibility) {
        this.parent = parent;
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.visibility = visibility;

        if (parent != null) {
            parent.children.add(this);
        }
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Number getMin() {
        return min;
    }

    public Number getMax() {
        return max;
    }

    public Value getParent() {
        return parent;
    }

    public boolean isVisible() {
        return visibility == null || visibility.get();
    }

    public static int current(Enum clazz) {
        for (int i = 0; i < clazz.getClass().getEnumConstants().length; ++i) {
            Enum e = ((Enum[]) clazz.getClass().getEnumConstants())[i];
            if (e.name().equalsIgnoreCase(clazz.name())) {
                return i;
            }
        }

        return -1;
    }

    public static Enum increase(Enum clazz) {
        int index = current(clazz);

        for (int i = 0; i < clazz.getClass().getEnumConstants().length; ++i) {
            Enum e = ((Enum[]) clazz.getClass().getEnumConstants())[i];
            if (i == index + 1) {
                return e;
            }
        }

        return ((Enum[]) clazz.getClass().getEnumConstants())[0];
    }
}