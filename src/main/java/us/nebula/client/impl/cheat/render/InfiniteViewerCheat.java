package us.nebula.client.impl.cheat.render;

import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatInstance;
import us.nebula.client.api.manager.cheat.CheatManifest;

/**
 * @author xgraza
 * @since 03/02/25
 */
@CheatManifest(name = "InfiniteViewer",
        description = "Allows you to see the true size of an item stack",
        category = CheatCategory.RENDER)
public final class InfiniteViewerCheat extends Cheat
{
    @CheatInstance
    public static InfiniteViewerCheat INSTANCE;
}
