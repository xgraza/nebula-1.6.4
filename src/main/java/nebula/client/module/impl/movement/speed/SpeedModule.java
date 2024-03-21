package nebula.client.module.impl.movement.speed;

import nebula.client.gui.module.future.setting.EnumSettingComponent;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.player.EventMove;
import nebula.client.listener.event.player.EventMultiUpdate;
import nebula.client.listener.event.player.EventUpdate;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.player.MoveUtils;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;

/**
 * @author Gavin
 * @since 08/23/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "Speed",
  description = "Gotta go fast")
public class SpeedModule extends Module {

  @SettingMeta("Mode")
  private final Setting<SpeedMode> mode = new Setting<>(
    SpeedMode.BHOP);
  @SettingMeta("Timer")
  private final Setting<Boolean> timer = new Setting<>(
    true);

  @SettingMeta("Updates")
  private final Setting<Integer> updates = new Setting<>(
    () -> mode.value() == SpeedMode.PHYSICS_CALC,
    4, 2, 50, 1);

  @SettingMeta("Fast Return")
  private final Setting<Boolean> fastReturn = new Setting<>(
    () -> mode.value() == SpeedMode.BHOP,
    false);

  private double speed, distance;
  private int stage, offGroundTicks;
  private boolean boost;

  @Override
  public void disable() {
    super.disable();

    speed = 0;
    distance = 0;
    stage = 1;
    offGroundTicks = 0;
    boost = false;
  }

  @Override
  public String info() {
    return EnumSettingComponent.format(mode.value());
  }

  @Subscribe
  private final Listener<EventMove> move = event -> {

    if (mode.value() == SpeedMode.BHOP) {

      if (timer.value()) {
        mc.timer.timerSpeed = 1.088f;
      }

      if (mc.thePlayer.onGround && MoveUtils.moving()) {
        offGroundTicks = 0;
        stage = 2;
      }

      if (!mc.thePlayer.onGround && MoveUtils.moving() && fastReturn.value()) {
        ++offGroundTicks;
        if (offGroundTicks > 3) {
          mc.thePlayer.motionY = (mc.thePlayer.motionY - 0.087) * 0.98f;
          event.setY(mc.thePlayer.motionY);
        }
      }

      switch (stage) {
        case 1 -> {
          speed = 1.38 * MoveUtils.ncpBaseSpeed() - 0.01;
          stage = 2;
        }

        case 2 -> {
          if (mc.thePlayer.onGround && MoveUtils.moving()) {
            float jumpHeight = MoveUtils.jumpHeight(0.42f);
            event.setY(jumpHeight);
            mc.thePlayer.motionY = jumpHeight;

            speed *= boost ? 1.6 : 1.44;
          }

          stage = 3;
        }

        case 3 -> {
          double bunny = 0.685 * (distance - MoveUtils.ncpBaseSpeed());
          speed = distance - bunny;
          boost = !boost;
          stage = 4;
        }

        default -> speed = distance - distance / 148.0;
      }

      speed = Math.max(MoveUtils.ncpBaseSpeed(), speed);
      if (MoveUtils.moving()) MoveUtils.strafe(event, speed);
    } else if (mode.value() == SpeedMode.Y_PORT) {
      speed = Math.max(MoveUtils.ncpBaseSpeed(), speed);
      if (MoveUtils.moving()) {
        MoveUtils.strafe(event, speed);
      }
    }
  };

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    distanceCalc: {
      double diffX = mc.thePlayer.posX - mc.thePlayer.prevPosX;
      double diffZ = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
      distance = Math.sqrt(diffX * diffX + diffZ * diffZ);
    }

    if (mode.value() == SpeedMode.Y_PORT) {
      speed = 1.25 * MoveUtils.ncpBaseSpeed() - 0.01;

      if (mc.thePlayer.onGround) {
        speed *= 1.3;
        mc.thePlayer.jump();
      } else {

        //speed = distance - distance / 50;

        mc.thePlayer.motionY = -4;
      }

    }
  };

  @Subscribe
  private final Listener<EventMultiUpdate> multiUpdate = event -> {
    if (mode.value() == SpeedMode.PHYSICS_CALC) {
      event.setUpdates(updates.value());
    }
  };
}
