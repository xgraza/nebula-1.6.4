package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 03/13/25
 */
@ModuleManifest(name = "AntiLag",
        description = "Prevents client-sided lag",
        category = ModuleCategory.PLAYER)
public final class AntiLagModule extends Module
{
    @ModuleInstance
    public static AntiLagModule INSTANCE;

    public final Setting<Boolean> groupItemsSetting = builder("Group Items", false)
            .setDescription("If to group dropped items together to prevent too many items from rendering at once")
            .build();
    public final Setting<Boolean> fallingBlocksSetting = builder("Render Falling Blocks", false)
            .setDescription("If to stop rendering falling blocks")
            .build();
    public final Setting<Boolean> noLightRecompile = builder("No Light Recompile", false)
            .setDescription("If to prevent light map recompilation every render tick")
            .build();
}
