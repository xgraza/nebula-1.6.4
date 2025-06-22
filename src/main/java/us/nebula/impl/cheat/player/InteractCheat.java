package us.nebula.impl.cheat.player;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;

/**
 * @author xgraza
 * @since 06/16/25
 */
@CheatManifest(name = "Interact",
        description = "Changes how you interact with things",
        category = CheatCategory.PLAYER)
public final class InteractCheat extends Cheat
{
    @CheatInstance
    public static InteractCheat INSTANCE;

    public final Setting<Boolean> waterPlaceSetting = new Setting<>(
            "Water Place", false);
}
