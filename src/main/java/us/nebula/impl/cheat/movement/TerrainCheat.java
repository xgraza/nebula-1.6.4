package us.nebula.impl.cheat.movement;

import net.minecraft.block.BlockStairs;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.player.EventMove;
import us.nebula.util.player.PlayerUtil;

/**
 * @author xgraza
 * @since 12/30/25
 */
@CheatManifest(name = "Terrain",
        description = "Changes movement based on the blocks you're on",
        category = CheatCategory.MOVEMENT)
public final class TerrainCheat extends Cheat
{
    @CheatInstance
    public static TerrainCheat INSTANCE;

    public static final float NCP_ICE_MAX = 0.391f;

    private final Setting<Boolean> iceSetting = new Setting<>(
            "Ice", false);
    private final Setting<Double> stairSpeedMultiplierSetting = new Setting<>(
            "Stair Speed Multi", 1.0, 1.0, 5.0, 0.1);

    @Subscribe
    private final EventListener<EventMove> moveEventListener = event ->
    {
        if (stairSpeedMultiplierSetting.getValue() > 1.0f && isOnStair())
        {
            final double multiplier = stairSpeedMultiplierSetting.getValue();
            event.setX(event.getX() * multiplier);
            event.setZ(event.getZ() * multiplier);
        }
    };

    private boolean isOnStair()
    {
        return MC.theWorld.getBlock(PlayerUtil.getOrigin().down()) instanceof BlockStairs;
    }

    public static boolean iceSpeed()
    {
        return INSTANCE != null && INSTANCE.isToggled() && INSTANCE.iceSetting.getValue();
    }
}
