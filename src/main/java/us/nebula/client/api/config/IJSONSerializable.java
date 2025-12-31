package us.nebula.client.api.config;

import com.google.gson.JsonElement;

/**
 * @author xgraza
 * @since 02/15/25
 */
public interface IJSONSerializable
{
    JsonElement toJSON();

    default void fromJSON(final JsonElement element)
    {

    }
}
