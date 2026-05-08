package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;

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

    public final Setting<Boolean> stopRenderSetting = new Setting<>(
            "Stop Render", true);
    public final Setting<Integer> fpsSetting = new Setting<>(
            "FPS", 15, 1, 60, 1);
}
