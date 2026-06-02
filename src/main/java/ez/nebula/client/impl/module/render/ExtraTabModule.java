package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 03/26/25
 */
@ModuleManifest(name = "ExtraTab",
        description = "Modifies how the server tab list appears",
        category = ModuleCategory.RENDER)
public final class ExtraTabModule extends Module
{
    @ModuleInstance
    public static ExtraTabModule INSTANCE;

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
