package lol.nebula.module.player;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.feature.DevelopmentFeature;
import lol.nebula.util.math.MathUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@DevelopmentFeature
public class Test extends Module {

    /**
     * Creates a new module object
     *
     * @param tag         the module tag
     * @param description the description of this module
     * @param category    the category this module belongs to
     */
    public Test() {
        super("Test", "lol", ModuleCategory.PLAYER);
    }

    @Listener
    public void onPacketOutbound(EventPacket.Outbound e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            e.cancel();
        }
    }
}
