package us.nebula.impl.cheat.player;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;

/**
 * @author xgraza
 * @since 03/13/25
 */
@CheatManifest(name = "AntiLag",
        description = "Prevents things in game from lagging you client-side",
        category = CheatCategory.PLAYER)
public final class AntiLagCheat extends Cheat
{
    @CheatInstance
    public static AntiLagCheat INSTANCE;

    public final Setting<Boolean> groupItemsSetting = new Setting<>(
            "Group Items", false);
    public final Setting<Boolean> fallingBlocksSetting = new Setting<>(
            "Render Falling Blocks", false);
}
