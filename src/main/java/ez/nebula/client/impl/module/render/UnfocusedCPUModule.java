package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 05/27/25
 */
@ModuleManifest(name = "UnfocusedCPU",
        description = "Reduces the load on your PC when unfocused",
        category = ModuleCategory.RENDER)
public final class UnfocusedCPUModule extends Module
{
    @ModuleInstance
    public static UnfocusedCPUModule INSTANCE;

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
