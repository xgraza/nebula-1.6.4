package us.nebula.impl.cheat.miscellaneous;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;

/**
 * @author xgraza
 * @since 04/11/25
 */
@CheatManifest(name = "BetterF3", category = CheatCategory.MISCELLANEOUS)
public final class BetterF3Cheat extends Cheat
{
    @CheatInstance
    public static BetterF3Cheat INSTANCE;
}
