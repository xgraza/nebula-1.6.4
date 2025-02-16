package us.nebula.api.manager.command.argument.type;

import us.nebula.api.manager.command.argument.Argument;
import us.nebula.api.manager.command.argument.Constraint;
import us.nebula.api.manager.command.exception.ArgumentResolveException;

/**
 * @author xgraza
 * @since 4.0.0
 * @param <T> number type
 */
@SuppressWarnings("unchecked")
public final class NumberArgument<T extends Number> extends Argument<T>
{
    public NumberArgument(final Class<T> type,
                          final String name,
                          final Constraint<T>... constraints)
    {
        super(type, name, constraints);
    }

    @Override
    public void resolve(final String raw) throws ArgumentResolveException
    {
        final Class<T> type = getType();
        try
        {
            if (Double.class.isAssignableFrom(type))
            {
                setValue((T) (Object) Double.parseDouble(raw));
            } else if (Float.class.isAssignableFrom(type))
            {
                setValue((T) (Object) Float.parseFloat(raw));
            } else if (Long.class.isAssignableFrom(type))
            {
                setValue((T) (Object) Long.parseLong(raw));
            } else if (Integer.class.isAssignableFrom(type))
            {
                setValue((T) (Object) Integer.parseInt(raw));
            } else
            {
                throw new ArgumentResolveException(this, "failed to resolve number type");
            }
        } catch (NumberFormatException e)
        {
            throw new ArgumentResolveException(this, "failed to parse input");
        }
    }
}
