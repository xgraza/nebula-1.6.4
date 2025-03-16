package us.nebula.impl.cheat.render;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;

/**
 * @author xgraza
 * @since 03/16/25
 */
@CheatManifest(name = "ItemPhysics",
        description = "Makes items render differently", // TODO: shit desc
        category = CheatCategory.RENDER)
public final class ItemPhysicsCheat extends Cheat
{
    @CheatInstance
    public static ItemPhysicsCheat INSTANCE;
}
