package ez.nebula.client.api.nws;

import ez.nebula.client.api.nws.packet.s2c.S2CInstruction;
import ez.nebula.client.api.nws.packet.s2c.S2COnlinePlayers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author xgraza
 * @since 9/3/26
 */
public final class NWSPacketHandler
{
    private static final Logger LOGGER = LogManager.getLogger("NWS PK Handler");

    private final NWS nws;

    public NWSPacketHandler(final NWS nws)
    {
        this.nws = nws;
    }

    public void handleS2COnlinePlayers(final S2COnlinePlayers packet)
    {
        nws.updateNebulaUsers(packet.getPlayers());
    }

    public void handleS2CInstruction(final S2CInstruction packet)
    {
        LOGGER.info("Received instruction {}", packet.getInstruction());
    }
}
