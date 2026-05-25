package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;

/**
 * @author xgraza
 * @since 03/26/25
 */
@CheatManifest(name = "ExtraTab",
        description = "Modifies how the server tab list appears",
        category = CheatCategory.RENDER)
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
