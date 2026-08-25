package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.util.render.world.EntityCulling;

/**
 * @author xgraza
 * @since 03/25/25
 */
@ModuleManifest(name = "EntityCulling",
        description = "Prevents rendering entities you cannot see to give a performance boost",
        category = ModuleCategory.RENDER)
public final class EntityCullingModule extends Module
{
    @ModuleInstance
    public static EntityCullingModule INSTANCE;

    @Override
    public void onDisable()
    {
        super.onDisable();
        EntityCulling.reset();
    }
}
