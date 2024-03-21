package nebula.client.module.impl.render.fullbright;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.player.EventUpdate;
import nebula.client.listener.event.render.EventWorldGamma;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import static java.lang.Integer.MAX_VALUE;

/**
 * @author Gavin
 * @since 03/20/24
 */
@ModuleMeta(name = "FullBright",
    description = "Makes the game bright")
public final class FullBrightModule extends Module {

  @SettingMeta("Mode")
  private final Setting<FullBrightMode> mode = new Setting<>(
      FullBrightMode.GAMMA);

  private boolean clientPotion;

  @Override
  public void disable() {
    super.disable();

    if (clientPotion
        && mc.thePlayer != null
        && mc.thePlayer.isPotionActive(Potion.nightVision)) {
      mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
    }

    clientPotion = false;
  }

  @Subscribe
  private final Listener<EventUpdate> updateListener = event -> {
    if (mode.value() == FullBrightMode.POTION) {

      if (!mc.thePlayer.isPotionActive(Potion.nightVision)) {
        clientPotion = true;

        final PotionEffect effect = new PotionEffect(
            Potion.nightVision.getId(), MAX_VALUE, 9999);
        effect.setPotionDurationMax(true);

        mc.thePlayer.addPotionEffect(effect);
      }

    } else {
      if (clientPotion && mc.thePlayer.isPotionActive(Potion.nightVision)) {
        mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
        clientPotion = false;
      }

    }
  };

  @Subscribe
  private final Listener<EventWorldGamma> worldGammaListener = event -> {
    if (mode.value() == FullBrightMode.GAMMA) {
      event.setGamma(10.0f);
    }
  };
}
