package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;

/**
 * @author xgraza
 * @since 03/16/25
 */
@CheatManifest(name = "ItemPhysics",
        description = "Makes items look like they have physics when dropped",
        category = CheatCategory.RENDER)
public final class ItemPhysicsCheat extends Cheat
{
    @CheatInstance
    public static ItemPhysicsCheat INSTANCE;
}
