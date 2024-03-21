package nebula.client.module.impl.combat.criticals;

import nebula.client.gui.module.future.setting.EnumSettingComponent;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.EventStage;
import nebula.client.listener.event.net.EventPacket;
import nebula.client.listener.event.player.EventMoveUpdate;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.math.Timer;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

/**
 * @author Gavin
 * @since 08/10/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "Criticals",
  description = "Makes your attacks critical hits")
public class CriticalsModule extends Module {

  @SettingMeta("Mode")
  private final Setting<CriticalsMode> mode = new Setting<>(
    CriticalsMode.MODIFY);
  @SettingMeta("Delay")
  private final Setting<Double> delay = new Setting<>(
    0.0, 0.0, 5.0, 0.01);

  private final Timer timer = new Timer();
  private int modifyStage = -1;

  @Override
  public void enable() {
    super.enable();
    modifyStage = -1;
  }

  @Override
  public String info() {
    return EnumSettingComponent.format(mode.value()) + " " + delay.value();
  }

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event -> {
    if (event.packet() instanceof C02PacketUseEntity packet) {
      if (packet.func_149565_c() != C02PacketUseEntity.Action.ATTACK) return;
      if (!(packet.func_149564_a(mc.theWorld) instanceof EntityLivingBase living)) return;

      // if we are not on ground, or in water, or we have blindness
      if (!mc.thePlayer.onGround
        || mc.thePlayer.isInWater()
        || mc.thePlayer.isInWeb
        || mc.thePlayer.isPotionActive(Potion.blindness)) return;

      // mc code balling
      if (living.hurtResistantTime < living.maxHurtResistantTime / 2.0f) return;

      if (!timer.ms((long) (delay.value() * 1000.0), false)) return;

      switch (mode.value()) {
        case PACKET -> {
          mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
            mc.thePlayer.posX, mc.thePlayer.boundingBox.minY + 0.1,
            mc.thePlayer.posY + 0.100000004768371, mc.thePlayer.posZ, false
          ));
          mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
            mc.thePlayer.posX, mc.thePlayer.boundingBox.minY,
            mc.thePlayer.posY, mc.thePlayer.posZ, false
          ));

          timer.resetTime();
        }

        case MODIFY -> {
          if (modifyStage == -1) {
            modifyStage = 0;
          }
        }
      }
    }
  };

  @Subscribe
  private final Listener<EventMoveUpdate> moveUpdate = event -> {
    if (event.stage() != EventStage.PRE) return;

    if (modifyStage != -1) {

      if (!mc.thePlayer.onGround) {
        modifyStage = -1;
        timer.resetTime();
        return;
      }

      event.setGround(false);
      switch (modifyStage++) {
        case 0 -> {
          event.setY(event.y() + 0.1);
          event.setStance(event.stance() + 0.100000004768371);
        }

        case 2 -> {
          event.setGround(true);
          timer.resetTime();
          modifyStage = -1;
        }
      }
    }
  };
}
