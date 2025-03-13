package us.nebula.impl.cheat.render;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;

/**
 * @author xgraza
 * @since 03/13/25
 */
@CheatManifest(name = "NoWeather",
        description = "Prevents weather from starting client-side",
        category = CheatCategory.RENDER)
public final class NoWeatherCheat extends Cheat
{
    @CheatInstance
    public static NoWeatherCheat INSTANCE;
}
