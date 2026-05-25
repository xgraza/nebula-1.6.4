package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.render.EntityCulling;

/**
 * @author xgraza
 * @since 03/25/25
 */
@CheatManifest(name = "EntityCulling",
        description = "Prevents rendering entities you cannot see to give a performance boost",
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
