package nebula.client.module.impl.render.noweather;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.render.weather.EventRainStrength;
import nebula.client.listener.event.render.weather.EventThunderStrength;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;

/**
 * @author Gavin
 * @since 08/24/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "NoWeather",
  description = "rain rain go away come back another day")
public class NoWeatherModule extends Module {

  @SettingMeta("Thunder")
  private final Setting<Boolean> thunder = new Setting<>(
    true);

  @Subscribe
  private final Listener<EventRainStrength> rainStrength
    = event -> event.setStrength(0.0f);

  @Subscribe
  private final Listener<EventThunderStrength> thunderStrength = event -> {
    if (!thunder.value()) return;
    event.setStrength(0.0f);
  };
}
