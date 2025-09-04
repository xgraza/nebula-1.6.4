package us.nebula.impl.cheat.render;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;

@CheatManifest(name = "ESP", category = CheatCategory.RENDER)
public final class ESPCheat extends Cheat
{
    private enum Mode
    {
        SIMPLE,
        CS_GO,
        SHADER
    }
}
