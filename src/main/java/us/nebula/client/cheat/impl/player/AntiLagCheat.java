package us.nebula.client.cheat.impl.player;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.setting.Setting;

/**
 * @author xgraza
 * @since 03/13/25
 */
@CheatManifest(name = "AntiLag",
        description = "Prevents client-sided lag",
        category = CheatCategory.PLAYER)
public final class AntiLagCheat extends Cheat
{
    @CheatInstance
    public static AntiLagCheat INSTANCE;

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
