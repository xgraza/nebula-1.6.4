package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.setting.Setting;

/**
 * @author xgraza
 * @since 03/24/25
 */
@CheatManifest(name = "TimeChanger",
        description = "Changes client-side time",
        category = CheatCategory.RENDER)
public final class TimeChangerCheat extends Cheat
{
    @CheatInstance
    public static TimeChangerCheat INSTANCE;

    public final Setting<Float> timeSetting = numberBuilder("Time", 0.0f)
            .setMin(0.0f)
            .setMax(24.0f)
            .setScale(0.25f)
            .setDescription("The time the world should be")
            .build();
}
