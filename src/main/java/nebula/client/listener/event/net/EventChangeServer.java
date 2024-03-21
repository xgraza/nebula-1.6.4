package nebula.client.listener.event.net;

import net.minecraft.client.multiplayer.ServerData;

/**
 * @author Gavin
 * @since 08/24/23
 */
public record EventChangeServer(ServerData serverData) {
}
