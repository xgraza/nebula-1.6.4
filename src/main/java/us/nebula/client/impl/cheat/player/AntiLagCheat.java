package us.nebula.client.impl.cheat.player;

import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatInstance;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;

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
    public final Setting<Boolean> noLightRecompile = new Setting<>(
            "No Light Recompile", false);
}
