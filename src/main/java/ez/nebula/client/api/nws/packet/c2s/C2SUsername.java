package ez.nebula.client.api.nws.packet.c2s;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.api.nws.packet.IPacket;

/**
 * @author xgraza
 * @since 9/3/26
 */
public final class C2SUsername implements IPacket
{
    private final String username;

    public C2SUsername(final String username)
    {
        this.username = username;
    }

    @Override
    public void read(final JsonElement element)
    {
        throw new RuntimeException("C2SPacketUsername is cannot be read, only sent");
    }

    @Override
    public JsonElement write()
    {
        final JsonObject object = new JsonObject();
        object.addProperty("username", username);
        return object;
    }

    @Override
    public int getType()
    {
        return 1;
    }
}
