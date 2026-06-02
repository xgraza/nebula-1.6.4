package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;

/**
 * @author xgraza
 * @since 04/11/25
 */
@ModuleManifest(name = "BetterF3",
        description = "Renders a more descriptive F3 debug menu",
        category = ModuleCategory.RENDER)
public final class BetterF3Module extends Module
{
    @ModuleInstance
    public static BetterF3Module INSTANCE;
}
