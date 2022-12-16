package wtf.nebula.client.impl.module.visuals;

import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class NoWeather extends ToggleableModule {
    public NoWeather() {
        super("No Weather", new String[]{"noweather", "antiweather", "norain", "nothunder"}, ModuleCategory.VISUALS);
    }
}
