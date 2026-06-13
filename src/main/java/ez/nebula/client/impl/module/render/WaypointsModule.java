package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 6/12/26
 */
@ModuleManifest(name = "Waypoints",
        description = "Configures waypoints on the client",
        category = ModuleCategory.RENDER)
public final class WaypointsModule extends Module
{
    @ModuleInstance
    public static WaypointsModule INSTANCE;

    public final Setting<Boolean> antiScreenshotSetting = builder("Anti-Screenshot", false)
            .setDescription("Prevent waypoints from being shown in a screenshot")
            .build();

    public WaypointsModule()
    {
        // automatically turn on & hide
        setHidden(true);
        setToggled(true);
    }
}
