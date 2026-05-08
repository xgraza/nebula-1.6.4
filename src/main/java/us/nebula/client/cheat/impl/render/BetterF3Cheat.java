package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;

/**
 * @author xgraza
 * @since 04/11/25
 */
@CheatManifest(name = "BetterF3",
        description = "Renders a more descriptive F3 debug menu",
        category = CheatCategory.RENDER)
public final class BetterF3Cheat extends Cheat
{
    @CheatInstance
    public static BetterF3Cheat INSTANCE;
}
