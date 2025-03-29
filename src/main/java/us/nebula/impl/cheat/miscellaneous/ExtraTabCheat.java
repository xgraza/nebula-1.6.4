package us.nebula.impl.cheat.miscellaneous;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;

/**
 * @author xgraza
 * @since 03/26/25
 */
@CheatManifest(name = "ExtraTab",
        description = "Modifies how the tab looks",
        category = CheatCategory.MISCELLANEOUS)
public final class ExtraTabCheat extends Cheat
{
    @CheatInstance
    public static ExtraTabCheat INSTANCE;

    public final Setting<Boolean> customSetting = new Setting<>(
            "Custom", false);
    public final Setting<Boolean> highlightFriendsSetting = new Setting<>(
            "Highlight Friends", true);
    public final Setting<Boolean> showPlayerHeadSetting = new Setting<>(
            "Player Heads", true);
    public final Setting<Boolean> showBarsSetting = new Setting<>(
            "Render Ping", true);
}
