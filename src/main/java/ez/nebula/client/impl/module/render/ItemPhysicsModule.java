package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;

/**
 * @author xgraza
 * @since 03/16/25
 */
@ModuleManifest(name = "ItemPhysics",
        description = "Makes items look like they have physics when dropped",
        category = ModuleCategory.RENDER)
public final class ItemPhysicsModule extends Module
{
    @ModuleInstance
    public static ItemPhysicsModule INSTANCE;
}
