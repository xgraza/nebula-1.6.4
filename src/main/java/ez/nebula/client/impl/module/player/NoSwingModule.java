package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 8/20/26
 */
@ModuleManifest(name = "NoSwing",
        description = "Prevents client-sided swinging from modules",
        category = ModuleCategory.PLAYER)
public final class NoSwingModule extends Module
{
    @ModuleInstance
    public static NoSwingModule INSTANCE;

    public final Setting<Boolean> clientSideSetting = builder("Client Side", false)
            .setDescription("If to also prevent client-sided swinging from your self")
            .build();
}
