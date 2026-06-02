package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;

import java.awt.Color;

/**
 * @author xgraza
 * @since 05/30/26
 */
@ModuleManifest(name = "Glint",
        description = "Changes the color of enchanted items when they glisten",
        category = ModuleCategory.RENDER)
public final class GlintModule extends Module
{
    @ModuleInstance
    public static GlintModule INSTANCE;

    public final Setting<Color> colorSetting = colorBuilder("Color", Color.white)
            .setDescription("What color to render the enchantment glisten with")
            .build();
}
