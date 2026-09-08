package ez.nebula.client.util.io;

import com.google.gson.JsonElement;

/**
 * @author xgraza
 * @since 02/15/25
 */
public interface IJSONSerializable
{
    default void fromJSON(final JsonElement element)
    {

    }

    JsonElement toJSON();
}
