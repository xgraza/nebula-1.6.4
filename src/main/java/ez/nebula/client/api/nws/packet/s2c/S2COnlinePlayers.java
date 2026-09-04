package ez.nebula.client.api.nws.packet.s2c;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import ez.nebula.client.api.nws.NWSPacketHandler;
import ez.nebula.client.api.nws.packet.IPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xgraza
 * @since 9/3/26
 */
public final class S2COnlinePlayers implements IPacket
{
    private final List<String> players = new ArrayList<>();

    @Override
    public void read(final JsonElement element)
    {
        if (!element.isJsonArray())
        {
            return;
        }
        final JsonArray array = element.getAsJsonArray();
        for (final JsonElement arrayElement : array)
        {
            if (!arrayElement.isJsonPrimitive())
            {
                continue;
            }
            final JsonPrimitive primitive = arrayElement.getAsJsonPrimitive();
            if (!primitive.isString())
            {
                continue;
            }
            players.add(primitive.getAsString());
        }
    }

    @Override
    public JsonElement write()
    {
        throw new RuntimeException("S2CPacketOnlinePlayers cannot be written to, only read");
    }

    @Override
    public void handle(final NWSPacketHandler handler)
    {
        handler.handleS2COnlinePlayers(this);
    }

    @Override
    public int getType()
    {
        return 1;
    }

    public List<String> getPlayers()
    {
        return players;
    }
}
