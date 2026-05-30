package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;

import java.awt.Color;

/**
 * @author xgraza
 * @since 05/30/26
 */
@CheatManifest(name = "Glint",
        description = "Changes the color of enchanted items when they glisten",
        category = CheatCategory.RENDER)
public final class GlintCheat extends Cheat
{
    @CheatInstance
    public static GlintCheat INSTANCE;

    public final Setting<Color> colorSetting = new Setting<>(
            "Color", Color.white);
}
