package us.nebula.api.manager.command.argument.type;

import us.nebula.api.manager.command.argument.Argument;
import us.nebula.api.manager.command.argument.Constraint;

import java.util.regex.Pattern;

/**
 * @author xgraza
 * @since 4.0.0
 */
public final class StringArgument extends Argument<String>
{
    @SafeVarargs
    public StringArgument(final String name,
                          final Constraint<String>... constraints)
    {
        super(String.class, name, constraints);
    }

    @Override
    public void resolve(final String raw)
    {
        setValue(raw);
    }

    public static Constraint<String> minMax(final int min, final int max)
    {
        if (min > max)
        {
            throw new RuntimeException("min cannot be greater than max");
        }
        if (min < 0)
        {
            throw new RuntimeException("min must be greater than 0");
        }
        return new Constraint<String>("Argument length must be between " + min + "-" + max)
        {
            @Override
            public boolean passes(final String raw)
            {
                final int length = raw.length();
                return length >= min && length <= max;
            }
        };
    }

    public static Constraint<String> matches(final Pattern pattern)
    {
        final String regex = pattern.pattern();
        return new Constraint<String>("Argument must match " + regex)
        {
            @Override
            public boolean passes(final String raw)
            {
                return pattern.matcher(raw).matches();
            }
        };
    }
}
