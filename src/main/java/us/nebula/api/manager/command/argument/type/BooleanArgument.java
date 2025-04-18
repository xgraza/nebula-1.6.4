package us.nebula.api.manager.command.argument.type;

import com.google.common.collect.Lists;
import us.nebula.api.manager.command.argument.Argument;
import us.nebula.api.manager.command.exception.ArgumentResolveException;

import java.util.List;

/**
 * @author xgraza
 * @since 03/19/25
 */
public final class BooleanArgument extends Argument<Boolean>
{
    private static final List<String> ACCEPTABLE_TRUE_VALUES = Lists.newArrayList(
            "true", "on", "yes");
    private static final List<String> ACCEPTABLE_FALSE_VALUES = Lists.newArrayList(
            "false", "off", "no");

    public BooleanArgument(final String name)
    {
        super(Boolean.class, name);
    }

    @Override
    public void resolve(final String raw) throws ArgumentResolveException
    {
        final String parsed = raw.toLowerCase();
        if (ACCEPTABLE_TRUE_VALUES.contains(parsed))
        {
            setValue(true);
        } else if (ACCEPTABLE_FALSE_VALUES.contains(parsed))
        {
            setValue(false);
        } else
        {
            throw new ArgumentResolveException(this, raw);
        }
    }

    @Override
    public List<String> computeSuggestions(final String input)
    {
        return super.computeSuggestions(input);
    }
}
