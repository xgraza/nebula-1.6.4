package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.setting.Setting;

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

    public final Setting<Boolean> customSetting = builder("Custom", false)
            .setDescription("If to completely override the default Minecraft tab list")
            .build();
    public final Setting<Boolean> highlightFriendsSetting = builder("Highlight Friends", true)
            .setDescription("If to highlight your friends names")
            .build();
    public final Setting<Boolean> showPlayerHeadSetting = builder("Player Heads", true)
            .setDescription("If to render player heads next to their name")
            .build();
    public final Setting<Boolean> showBarsSetting = builder("Render Ping", true)
            .setDescription("If to render the latency icon next to a player's name")
            .build();
}
