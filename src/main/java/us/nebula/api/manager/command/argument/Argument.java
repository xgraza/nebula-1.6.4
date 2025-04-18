package us.nebula.api.manager.command.argument;

import com.google.common.collect.Lists;
import us.nebula.api.manager.command.exception.ArgumentResolveException;

import java.util.*;

/**
 * @author xgraza
 * @since 1.0.0
 */
@SuppressWarnings("all")
public abstract class Argument<T>
{
    private final String name;
    private final Class<T> type;
    protected T value;

    private boolean required, dependant;

    private final Map<String, Argument<?>> dependants = new LinkedHashMap<>();
    private final List<Argument<?>> dependantsList = new LinkedList<>();
    private ArgumentDispatcher dispatcher;

    private final List<Constraint<T>> constraints;

    public Argument(final Class<T> type,
                    final String name,
                    final Constraint<T>... constraints)
    {
        this.name = name;
        this.type = type;
        this.constraints = Lists.newArrayList(constraints);

        required = true;
    }

    public String passes(final String raw)
    {
        if (constraints.isEmpty())
        {
            return null;
        }
        for (final Constraint<T> constraint : constraints)
        {
            if (!constraint.passes(raw))
            {
                return constraint.getFailReason();
            }
        }
        return null;
    }

    public abstract void resolve(final String raw) throws ArgumentResolveException;

    public List<String> computeSuggestions(final String input)
    {
        return Collections.emptyList();
    }

    public String getName()
    {
        return name;
    }

    public Class<T> getType()
    {
        return type;
    }

    public void setValue(T value)
    {
        this.value = value;
    }

    public T getValue()
    {
        return value;
    }

    public boolean isResolved()
    {
        if (isRequired())
        {
            return true;
        }
        if (!dependants.isEmpty())
        {
            for (final Argument<?> dependant : dependants.values())
            {
                if (!dependant.isResolved())
                {
                    return false;
                }
            }
        }
        return value != null;
    }

    public void clearValue()
    {
        value = null;
    }

    public Argument<T> setRequired(boolean required)
    {
        this.required = required;
        return this;
    }

    public boolean isRequired()
    {
        return required;
    }

    public boolean isDependant()
    {
        return dependant;
    }

    public void setDependant(boolean dependant)
    {
        this.dependant = dependant;
    }

    public void addDependant(final Argument<?> argument)
    {
        dependantsList.add(argument);
        dependants.put(argument.getName(), argument);
    }

    @SuppressWarnings("unchecked")
    public <V> Argument<V> getDependant(final String name)
    {
        return (Argument<V>) dependants.get(name);
    }

    public List<Argument<?>> getDependants()
    {
        return dependantsList;
    }

    public void setArgumentExecutor(ArgumentDispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    public ArgumentDispatcher getArgumentExecutor()
    {
        return dispatcher;
    }
}
