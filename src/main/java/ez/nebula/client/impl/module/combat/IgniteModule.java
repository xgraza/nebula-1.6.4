package ez.nebula.client.impl.module.combat;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.NumberSetting;

/**
 * @author xgraza
 * @since 7/31/26
 */
@ModuleManifest(name = "Ignite",
        description = "Automatically sets another player on fire with a flint and steel",
        category = ModuleCategory.COMBAT)
public final class IgniteModule extends Module
{
    private final NumberSetting<Double> rangeSetting = numberBuilder("Range", 4.5)
            .setMin(1.0)
            .setMax(6.0)
            .setScale(0.5)
            .setDescription("The place range")
            .build();

    
}
