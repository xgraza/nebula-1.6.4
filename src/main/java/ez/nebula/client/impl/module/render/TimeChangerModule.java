package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.NumberSetting;

/**
 * @author xgraza
 * @since 03/24/25
 */
@ModuleManifest(name = "TimeChanger",
        description = "Changes client-side time",
        category = ModuleCategory.RENDER)
public final class TimeChangerModule extends Module
{
    @ModuleInstance
    public static TimeChangerModule INSTANCE;

    public final NumberSetting<Float> timeSetting = numberBuilder("Time", 0.0f)
            .setMin(0.0f)
            .setMax(24.0f)
            .setScale(0.25f)
            .setDescription("The time the world should be")
            .build();
}
