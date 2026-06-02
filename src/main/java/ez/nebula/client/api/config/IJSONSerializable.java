package ez.nebula.client.api.config;

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
