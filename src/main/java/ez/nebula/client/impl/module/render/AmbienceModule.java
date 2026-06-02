package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 5/16/26
 */
@ModuleManifest(name = "Ambience",
        description = "Adds a colored tint to your world",
        category = ModuleCategory.RENDER)
public final class AmbienceModule extends Module
{
    @ModuleInstance
    public static AmbienceModule INSTANCE;

    public final Setting<Double> intensitySetting = numberBuilder("Intensity", 0.95)
            .setMin(0.1)
            .setMax(1.0)
            .setScale(0.05)
            .setDescription("How intense the color should be")
            .build();
}
