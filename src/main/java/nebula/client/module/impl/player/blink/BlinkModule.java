package nebula.client.module.impl.player.blink;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.net.EventPacket;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.player.FakePlayerUtils;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Gavin
 * @since 08/24/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "Blink",
  description = "Holds packets until a later time")
public class BlinkModule extends Module {

  // packets
  @SettingMeta("Movement")
  private final Setting<Boolean> movement = new Setting<>(
    true);
  @SettingMeta("Keep Alive")
  private final Setting<Boolean> keepAlive = new Setting<>(
    false);
  @SettingMeta("Actions")
  private final Setting<Boolean> actions = new Setting<>(
    true);

  // other
  @SettingMeta("Resend")
  private final Setting<Boolean> resend = new Setting<>(
    true);
  @SettingMeta("Render")
  private final Setting<Boolean> render = new Setting<>(
    true);

  private final Queue<Packet> packetQueue = new ConcurrentLinkedQueue<>();

  @Override
  public void disable() {
    super.disable();

    if (resend.value() && mc.thePlayer != null) {
      while (!packetQueue.isEmpty()) {
        mc.thePlayer.sendQueue.addToSendQueueSilent(packetQueue.poll());
      }
    }

    packetQueue.clear();
    FakePlayerUtils.despawn(hashCode());
  }

  @Override
  public void enable() {
    super.enable();

    if (mc.thePlayer == null) {
      macro().setEnabled(false);
      return;
    }

    FakePlayerUtils.spawn(hashCode(), mc.thePlayer);
  }

  @Override
  public String info() {
    return String.valueOf(packetQueue.size());
  }

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event -> {
    if (mc.thePlayer == null || mc.thePlayer.ticksExisted < 5) {
      packetQueue.clear();
      return;
    }

    if ((event.packet() instanceof C03PacketPlayer && movement.value())
      || (event.packet() instanceof C00PacketKeepAlive && keepAlive.value())
      || (event.packet() instanceof C0BPacketEntityAction && actions.value())) {

      packetQueue.add(event.packet());
      event.setCanceled(true);
    }
  };
}
