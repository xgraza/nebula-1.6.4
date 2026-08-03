package ez.nebula.client.api.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author xgraza
 * @since 7/31/26
 * @param <T> the type for this {@link MultiSetting}
 */
public final class MultiSetting<T> extends Setting<List<T>>
{
    public MultiSetting(String name, String description, Predicate<List<T>> visibility, Consumer<List<T>> valueChanged, List<T> value)
    {
        super(name, description, visibility, valueChanged, value);
    }

    public MultiSetting(String name, String description, Predicate<List<T>> visibility, Consumer<List<T>> valueChanged)
    {
        super(name, description, visibility, valueChanged, new ArrayList<>());
    }

    public Optional<T> get(final Predicate<T> filter)
    {
        return getValue().stream().filter(filter).findFirst();
    }

    public void add(final T item)
    {
        getValue().add(item);
    }

    public void remove(final T item)
    {
        getValue().remove(item);
    }

    public boolean has(final T item)
    {
        return getValue().contains(item);
    }
}
