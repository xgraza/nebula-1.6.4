package us.nebula.impl.cheat.render;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CheatManifest(name = "ClickGUI", category = CheatCategory.RENDER)
public final class ClickGUICheat extends Cheat
{
    @CheatInstance
    public static ClickGUICheat INSTANCE;
}
