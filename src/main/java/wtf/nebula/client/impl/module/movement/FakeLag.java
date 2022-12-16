package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeLag extends ToggleableModule {
    private final Property<Boolean> manual = new Property<>(false, "Manual");
    private final Property<Boolean> all = new Property<>(false, "All", "allpackets");
    private final Property<Double> time = new Property<>(5.0, 0.1, 20.0, "Time", "t", "seconds", "sec")
            .setVisibility(() -> !manual.getValue());

    private final Map<Long, Packet> packetMap = new ConcurrentHashMap<>();

    public FakeLag() {
        super("Fake Lag", new String[]{"fakelag", "blink", "holdpackets"}, ModuleCategory.MOVEMENT);
        offerProperties(manual, all, time);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        packetMap.forEach((t, p) -> mc.thePlayer.sendQueue.addToSendQueueSilent(p));
        packetMap.clear();
    }

    @Override
    public String getTag() {
        return String.valueOf(packetMap.size());
    }

    @EventListener
    public void onTick(EventTick event) {

        if (mc.thePlayer.ticksExisted < 10) {
            return;
        }

        if (!manual.getValue()) {

            packetMap.forEach((t, packet) -> {

                if ((System.nanoTime() - t) / 1000000L > (long) (time.getValue() * 1000.0)) {
                    mc.thePlayer.sendQueue.addToSendQueueSilent(packet);
                    packetMap.remove(t);
                }

            });
        }
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (isNull() || mc.thePlayer.ticksExisted < 10) {
            return;
        }

        if (all.getValue() || event.getPacket() instanceof C03PacketPlayer) {
            event.setCancelled(true);
            packetMap.put(System.nanoTime(), event.getPacket());
        }
    }
}
