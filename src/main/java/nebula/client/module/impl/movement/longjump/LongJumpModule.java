package nebula.client.module.impl.movement.longjump;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.net.EventPacket;
import nebula.client.listener.event.player.EventMove;
import nebula.client.listener.event.player.EventUpdate;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.player.MoveUtils;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

/**
 * @author Gavin
 * @since 08/26/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "LongJump",
  description = "Makes you jump long and shit")
public class LongJumpModule extends Module {

  @SettingMeta("Boost")
  private final Setting<Double> boost = new Setting<>(
    4.5, 1.0, 6.0, 0.1);

  private double speed, distance;
  private int stage;

  @Override
  public void disable() {
    super.disable();

    speed = 0;
    distance = 0;
    stage = 1;
  }

  @Override
  public String info() {
    return String.valueOf(boost.value());
  }

  @Subscribe
  private final Listener<EventMove> move = event -> {

    switch (stage) {
      case 1 -> {
        speed = boost.value() * MoveUtils.ncpBaseSpeed() - 0.01;
        stage = 2;
      }

      case 2 -> {
        if (mc.thePlayer.onGround && MoveUtils.moving()) {
          event.setY(0.42f);
          mc.thePlayer.motionY = event.y();

          speed *= 2.149;
        }

        stage = 3;
      }

      case 3 -> {
        double bunny = 0.86 * (distance - MoveUtils.ncpBaseSpeed());
        speed = distance - bunny;
        stage = 4;
      }

      case 4 -> speed = distance - distance / 159.0;
    }

    speed = Math.max(speed, MoveUtils.ncpBaseSpeed());
    if (MoveUtils.moving()) MoveUtils.strafe(event, speed);
  };

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    double diffX = mc.thePlayer.posX - mc.thePlayer.prevPosX;
    double diffZ = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
    distance = Math.sqrt(diffX * diffX + diffZ * diffZ);
  };

  @Subscribe
  private final Listener<EventPacket.In> packetIn = event -> {
    if (event.packet() instanceof S08PacketPlayerPosLook) {
      macro().setEnabled(false);
    }
  };
}
