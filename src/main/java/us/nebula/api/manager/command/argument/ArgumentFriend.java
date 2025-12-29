package us.nebula.api.manager.command.argument;

import us.xgraza.xcmd.parser.argument.Argument;
import us.xgraza.xcmd.parser.argument.exception.ArgumentParseException;

/**
 * @author xgraza
 * @since 08/13/25
 */
public final class ArgumentFriend extends Argument<String>
{
    public ArgumentFriend(String name)
    {
        super(String.class, name);
    }

    @Override
    public String parse(final String raw, final String type) throws ArgumentParseException
    {
        return "";
    }
}
