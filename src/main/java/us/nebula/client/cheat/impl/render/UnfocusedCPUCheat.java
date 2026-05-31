package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.setting.Setting;

/**
 * @author xgraza
 * @since 05/27/25
 */
@CheatManifest(name = "UnfocusedCPU",
        description = "Reduces the load on your PC when unfocused",
        category = CheatCategory.RENDER)
public final class UnfocusedCPUCheat extends Cheat
{
    @CheatInstance
    public static UnfocusedCPUCheat INSTANCE;

    public final Setting<Boolean> stopRenderSetting = builder("Stop Render", true)
            .setDescription("If to stop rendering the game entirely when unfocused")
            .build();
    public final Setting<Integer> fpsSetting = numberBuilder("FPS", 15)
            .setMin(1)
            .setMax(60)
            .setScale(1)
            .setDescription("The maximum FPS cap when unfocused on the game")
            .build();
}
