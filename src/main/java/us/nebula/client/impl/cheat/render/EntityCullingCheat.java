package us.nebula.client.impl.cheat.render;

import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatInstance;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.render.EntityCulling;

/**
 * @author xgraza
 * @since 03/25/25
 */
@CheatManifest(name = "EntityCulling",
        description = "Prevents rendering entities you cannot see",
        category = CheatCategory.RENDER)
public final class EntityCullingCheat extends Cheat
{
    @CheatInstance
    public static EntityCullingCheat INSTANCE;

    @Override
    public void onDisable()
    {
        super.onDisable();
        EntityCulling.reset();
    }
}
