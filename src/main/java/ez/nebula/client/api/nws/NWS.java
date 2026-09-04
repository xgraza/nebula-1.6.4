package ez.nebula.client.api.nws;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.api.nws.packet.IPacket;
import ez.nebula.client.api.nws.packet.Packets;
import ez.nebula.client.util.io.FileUtil;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xgraza
 * @since 9/2/26
 */
public final class NWS extends WebSocketClient
{
    private static final Logger LOGGER = LogManager.getLogger("NWS");
    private static final URI SERVER_URI = URI.create("ws://localhost:8080");

    public static NWS INSTANCE = new NWS();

    private final NWSPacketHandler packetHandler = new NWSPacketHandler(this);
    private final Set<String> nebulaPlayerList = new HashSet<>();

    public NWS()
    {
        super(SERVER_URI);
        setTcpNoDelay(true);
        setProxy(Minecraft.getMinecraft().getProxy());
    }

    @Override
    public void onOpen(final ServerHandshake handshake)
    {
        LOGGER.info("### Connecting to Nebula Websocket ###");
        LOGGER.info("\tLocation: {}", SERVER_URI);
        LOGGER.info("\t({}) -> {}", handshake.getHttpStatus(), handshake.getHttpStatusMessage());
    }

    @Override
    public void onMessage(final ByteBuffer bytes)
    {
        final String data = new String(bytes.array());
        if (data.isEmpty())
        {
            LOGGER.warn("Malformed byte data");
            return;
        }
        readIncoming(data);
    }

    @Override
    public void onMessage(final String data)
    {
        if (data.isEmpty())
        {
            LOGGER.warn("Malformed message data");
            return;
        }
        readIncoming(data);
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        LOGGER.info("NWS closed: ({}) -> {} (r: {})", code, reason, remote);
    }

    @Override
    public void onError(final Exception e)
    {
        LOGGER.error("Something failed!", e);
    }

    private void readIncoming(final String data)
    {
        final JsonElement element = FileUtil.JSON_PARSER.parse(data);
        if (element == null || !element.isJsonObject())
        {
            LOGGER.warn("Unrecognized packet data");
            return;
        }
        final JsonObject payload = element.getAsJsonObject();
        if (!payload.has("t") || !payload.has("d"))
        {
            LOGGER.warn("Payload object is missing type or data");
            return;
        }
        final int t = payload.get("t").getAsInt();
        try
        {
            final IPacket packet = Packets.createServerPacket(t, payload.get("d"));
            if (packet == null)
            {
                LOGGER.warn("Unrecognized server packet w/ type {}", t);
                return;
            }
            packet.handle(packetHandler);
        } catch (final Exception e)
        {
            LOGGER.warn("Failed to read/handle a packet", e);
        }
    }

    public void sendPacket(final IPacket packet)
    {
        if (!isOpen())
        {
            LOGGER.warn("Attempted to send {} while not open", packet.getClass().getSimpleName());
            return;
        }
        try
        {
            final JsonObject payload = new JsonObject();
            payload.addProperty("t", packet.getType());
            payload.add("d", packet.write());
            final String data = payload.toString();
            if (data.isEmpty())
            {
                LOGGER.warn("Empty packet payload ({})", packet.getClass().getSimpleName());
                return;
            }
            send(data.getBytes(StandardCharsets.UTF_8));
        } catch (final Exception e)
        {
            LOGGER.error("Failed to write packet payload", e);
        }
    }

    void updateNebulaUsers(final List<String> players)
    {
        nebulaPlayerList.clear();
        nebulaPlayerList.addAll(players);
    }

    public boolean isNebulaUser(final String username)
    {
        return nebulaPlayerList.contains(username);
    }

    public static void start(final boolean reconnect)
    {
        if (reconnect || INSTANCE == null)
        {
            if (INSTANCE != null)
            {
                INSTANCE.close();
            }
            INSTANCE = new NWS();
        }
        LOGGER.info("Starting NWS");
        INSTANCE.connect();
    }
}
