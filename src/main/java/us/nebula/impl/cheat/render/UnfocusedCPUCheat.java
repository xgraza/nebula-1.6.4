package us.nebula.impl.cheat.render;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;

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
