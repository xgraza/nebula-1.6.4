package us.nebula.client.impl.cheat.render;

import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatInstance;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;

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

    public final Setting<Float> timeSetting = new Setting<>(
            "Time", 0.0f, 0.0f, 24.0f, 0.25f);
}
