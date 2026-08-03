package ez.nebula.client.impl.module.combat;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;

/**
 * @author xgraza
 * @since 7/31/26
 */
@ModuleManifest(name = "Ignite",
        description = "Automatically sets another player on fire with a flint and steel",
        category = ModuleCategory.COMBAT)
public final class IgniteModule extends Module
{
}
