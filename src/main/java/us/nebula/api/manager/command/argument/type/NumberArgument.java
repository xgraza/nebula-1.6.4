package us.nebula.api.manager.command.argument.type;

import us.nebula.api.manager.command.argument.Argument;
import us.nebula.api.manager.command.exception.ArgumentResolveException;

/**
 * @author xgraza
 * @since 4.0.0
 * @param <T> number type
 */
@SuppressWarnings("unchecked")
public final class NumberArgument<T extends Number> extends Argument<T>
{
    public NumberArgument(Class<T> type, String name)
    {
        super(type, name);
    }

    @Override
    public void resolve(String input) throws ArgumentResolveException
    {
        final Class<T> type = getType();
        try
        {
            if (Double.class.isAssignableFrom(type))
            {
                setValue((T) (Object) Double.parseDouble(input));
            } else if (Float.class.isAssignableFrom(type))
            {
                setValue((T) (Object) Float.parseFloat(input));
            } else if (Long.class.isAssignableFrom(type))
            {
                setValue((T) (Object) Long.parseLong(input));
            } else if (Integer.class.isAssignableFrom(type))
            {
                setValue((T) (Object) Integer.parseInt(input));
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
