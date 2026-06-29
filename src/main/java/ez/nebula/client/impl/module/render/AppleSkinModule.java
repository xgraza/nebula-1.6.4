package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.ColorSetting;
import ez.nebula.client.api.setting.Setting;

import java.awt.Color;

/**
 * @author xgraza
 * @since 6/221/26
 */
@ModuleManifest(name = "AppleSkin",
        description = "Renders your hunger saturation value over the hunger bar",
        category = ModuleCategory.RENDER)
public final class AppleSkinModule extends Module
{
    @ModuleInstance
    public static AppleSkinModule INSTANCE;

    public final ColorSetting colorSetting = colorBuilder("Color", new Color(230, 199, 43))
            .setDescription("The color to render the saturation bar with")
            .build();
}
