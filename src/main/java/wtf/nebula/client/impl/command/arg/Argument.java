package wtf.nebula.client.impl.command.arg;

public abstract class Argument<T> {
    private final Class<T> clazz;
    private final String name;
    private T value;

    private boolean required = true;

    private boolean lastParseResult = false;

    public Argument(String name, Class<T> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public abstract boolean parse(String part);

    public String getName() {
        return name;
    }

    public Argument setValue(T value) {
        this.value = value;
        return this;
    }

    public T getValue() {
        return value;
    }

    public Class<T> getType() {
        return clazz;
    }

    public boolean isRequired() {
        return required;
    }

    public Argument setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public void setLastParseResult(boolean lastParseResult) {
        this.lastParseResult = lastParseResult;
    }

    public boolean getLastParseResult() {
        return lastParseResult;
    }
}
