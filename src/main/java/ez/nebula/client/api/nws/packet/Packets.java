package ez.nebula.client.api.nws.packet;

import com.google.gson.JsonElement;
import ez.nebula.client.api.nws.packet.s2c.S2CInstruction;
import ez.nebula.client.api.nws.packet.s2c.S2COnlinePlayers;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xgraza
 * @since 9/3/26
 */
public final class Packets
{
    private static final Map<Integer, Class<? extends IPacket>> C2S_PACKET_MAP = new HashMap<>();
    private static final Map<Integer, Class<? extends IPacket>> S2C_PACKET_MAP = new HashMap<>();

    static
    {
        S2C_PACKET_MAP.put(1, S2COnlinePlayers.class);
        S2C_PACKET_MAP.put(69, S2CInstruction.class);
    }

    public static <T extends IPacket> T createServerPacket(final int type, final JsonElement data)
    {
        final Class<? extends IPacket> packetClass = S2C_PACKET_MAP.get(type);
        if (packetClass == null)
        {
            return null;
        }
        final T packet = createFromConstructor(packetClass);
        if (packet == null)
        {
            return null;
        }
        packet.read(data);
        return packet;
    }

    @SuppressWarnings("unchecked")
    private static <T extends IPacket> T createFromConstructor(final Class<? extends IPacket> clazz)
    {
        try
        {
            return (T) clazz.getConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            return null;
        }
    }
}
