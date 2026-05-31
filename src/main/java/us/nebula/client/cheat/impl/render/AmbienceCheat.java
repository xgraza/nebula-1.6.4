package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.setting.Setting;

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

    public final Setting<Double> intensitySetting = numberBuilder("Intensity", 0.95)
            .setMin(0.1)
            .setMax(1.0)
            .setScale(0.05)
            .setDescription("How intense the color should be")
            .build();
}
