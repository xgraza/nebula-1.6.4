package wtf.nebula.impl.value;

import wtf.nebula.util.feature.ToggleableFeature;

import java.util.*;

/**
 * A container that all settings can be implemented into
 *
 * @author aesthetical
 * @since 06/27/22
 */
public class ValueContainer extends ToggleableFeature {
    protected final List<Value> values = new ArrayList<>();
    protected final Map<String, Value> valueMap = new LinkedHashMap<>();

    public ValueContainer(String name) {
        super(name);
    }

    /**
     * Reflects all values in this class if any
     */
    public void reflectValues() {
        Arrays.stream(getClass().getDeclaredFields())
                .filter((field) -> Value.class.isAssignableFrom(field.getType()))
                .forEach((field) -> {
                    // change accessibility of field if private
                    field.setAccessible(true);

                    try {
                        register((Value) field.get(this));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * Registers a value to this container
     * @param value the value
     * @param <T> the value type
     * @return the value
     */
    protected <T extends Value> T register(Value value) {
        values.add(value);
        valueMap.put(value.getName(), value);

        return (T) value;
    }

    /**
     * Gets a value based on a name
     * @param name the name of the value
     * @param <T> the value type
     * @return the found value corresponding to that name or null
     */
    public <T extends Value> T getValue(String name) {
        return (T) valueMap.getOrDefault(name, null);
    }

    public Map<String, Value> getValueMap() {
        return valueMap;
    }

    public List<Value> getValues() {
        return values;
    }
}
