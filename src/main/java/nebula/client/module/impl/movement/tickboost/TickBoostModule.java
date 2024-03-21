package nebula.client.module.impl.movement.tickboost;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.net.EventPacket;
import nebula.client.listener.event.player.EventMove;
import nebula.client.listener.event.player.EventUpdate;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.chat.Printer;
import nebula.client.util.player.MoveUtils;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Gavin
 * @since 08/24/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "TickBoost", description = "lol")
public class TickBoostModule extends Module {

  @SettingMeta("Boost")
  private final Setting<Float> boost = new Setting<>(
    0.5f, 0.1f, 5.0f, 0.01f);
  @SettingMeta("Ticks")
  private final Setting<Integer> ticks = new Setting<>(
    20, 1, 200, 1);

  @SettingMeta("Auto Charge")
  private final Setting<Boolean> autoCharge = new Setting<>(
    false);
  @SettingMeta("Cancel Packets")
  private final Setting<Boolean> cancelPackets = new Setting<>(
    false);

  private int boostTicks;
  private boolean waitForCharge;

  @Override
  public void disable() {
    super.disable();
    boostTicks = 0;
    mc.timer.timerSpeed = 1.0f;
    waitForCharge = true;
  }

  @Override
  public String info() {
    return String.valueOf(boostTicks);
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (MoveUtils.speed() > 0.0) {
      if (boostTicks > 0) {
        waitForCharge = false;
        --boostTicks;
        mc.timer.timerSpeed = 1.0f + boost.value();
      } else {
        waitForCharge = true;
        mc.timer.timerSpeed = 1.0f;
      }
    } else {
      ++boostTicks;
      if (boostTicks > ticks.value()) {
        waitForCharge = false;
        boostTicks = ticks.value();
      }
    }
  };

  @Subscribe
  private final Listener<EventMove> move = event -> {
    if (autoCharge.value() && waitForCharge) {
      MoveUtils.strafe(event, 0);
    }

    if (!waitForCharge) {
      MoveUtils.strafe();
    }
  };

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event -> {
    if (waitForCharge
      && cancelPackets.value()
      && event.packet() instanceof C03PacketPlayer) {
      event.setCanceled(true);
    }
  };
}
