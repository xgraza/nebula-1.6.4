package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;

/**
 * @author xgraza
 * @since 5/16/26
 */
@CheatManifest(name = "Ambience",
        description = "Adds a colored tint to your world",
        category = CheatCategory.RENDER)
public final class AmbienceCheat extends Cheat
{
    @CheatInstance
    public static AmbienceCheat INSTANCE;

    public final Setting<Double> intensitySetting = new Setting<>(
            "Intensity", 0.95, 0.1, 1.0, 0.05);
}
