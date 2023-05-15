package lol.nebula.module.player;

import com.google.common.collect.Lists;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.listener.events.render.world.EventEnchantmentBookRender;
import lol.nebula.listener.events.render.world.EventRenderFallingBlock;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S0EPacketSpawnObject;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author aesthetical
 * @since 05/15/23
 */
public class AntiLag extends Module {
    // potion, exp, egg, snowballs
    private static final List<Integer> SPAWN_BLACKLIST = Lists.newArrayList(73, 75, 62, 61);

    public AntiLag() {
        super("Anti Lag", "Stops you from lagging out from server-side actions", ModuleCategory.PLAYER);
    }

    @Listener
    public void onPacketInbound(EventPacket.Inbound event) {
        if (event.getPacket() instanceof S0EPacketSpawnObject) {
            S0EPacketSpawnObject packet = event.getPacket();
            if (SPAWN_BLACKLIST.contains(packet.func_148993_l())) event.cancel();
        } else if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = event.getPacket();
            int size = packet.func_148915_c()
                    .getUnformattedText()
                    .getBytes(StandardCharsets.UTF_8).length;
            if (size > 355) {
                print("Canceled message from server with message size greater than 355 bytes");
                event.cancel();
            }
        }
    }

    @Listener
    public void onRenderFallingBlock(EventRenderFallingBlock event) {
        event.cancel();
    }

    @Listener
    public void onEnchantmentBookRender(EventEnchantmentBookRender event) {
        event.cancel();
    }
}
