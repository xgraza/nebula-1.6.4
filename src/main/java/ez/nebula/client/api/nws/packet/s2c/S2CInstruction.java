package ez.nebula.client.api.nws.packet.s2c;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.api.nws.NWSPacketHandler;
import ez.nebula.client.api.nws.packet.IPacket;

/**
 * @author xgraza
 * @since 9/4/26
 */
public final class S2CInstruction implements IPacket
{
    private int instruction;

    @Override
    public void read(final JsonElement element)
    {
        if (!element.isJsonObject())
        {
            return;
        }
        final JsonObject object = element.getAsJsonObject();
        if (!object.has("i"))
        {
            throw new RuntimeException("S2CInstruction is missing an \"i\" parameter");
        }
        instruction = object.get("i").getAsInt();
    }

    @Override
    public JsonElement write()
    {
        throw new RuntimeException("S2CInstruction cannot be written to, only read");
    }

    @Override
    public void handle(final NWSPacketHandler handler)
    {
        handler.handleS2CInstruction(this);
    }

    @Override
    public int getType()
    {
        return 69;
    }

    public int getInstruction()
    {
        return instruction;
    }
}
