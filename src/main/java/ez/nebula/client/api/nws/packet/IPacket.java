package ez.nebula.client.api.nws.packet;

import com.google.gson.JsonElement;
import ez.nebula.client.api.nws.NWSPacketHandler;

/**
 * @author xgraza
 * @since 9/2/26
 */
public interface IPacket
{
    void read(final JsonElement element);

    JsonElement write();

    default void handle(NWSPacketHandler handler)
    {

    }

    int getType();
}
