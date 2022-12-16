package wtf.nebula.client.impl.command.arg.impl;

import wtf.nebula.client.impl.command.arg.Argument;

public class NumberArgument<T extends Number> extends Argument<T> {
    private T defaultValue;

    public NumberArgument(String name) {
        super(name, (Class<T>) Number.class);
    }

    @Override
    public boolean parse(String part) {
        try {
             if (getType().isAssignableFrom(Double.class)) {
                setValue((T) Double.valueOf(part));
            } else if (getType().isAssignableFrom(Float.class)) {
                 setValue((T) Float.valueOf(part));
            } if (getType().isAssignableFrom(Integer.class)) {
                setValue((T) Integer.valueOf(part));
            }

            return true;
        } catch (NumberFormatException e) {
            setValue(defaultValue);
            return false;
        }
    }

    public NumberArgument<T> setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
}
