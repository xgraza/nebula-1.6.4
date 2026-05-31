package us.nebula.client.cheat.impl.movement;

import net.minecraft.block.BlockStairs;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.player.EventMove;
import us.nebula.client.setting.Setting;
import us.nebula.client.util.player.PlayerUtil;

/**
 * @author xgraza
 * @since 12/30/25
 */
@CheatManifest(name = "Terrain",
        description = "Changes how your player behaves on specific terrain (i.e. ice, stairs)",
        category = CheatCategory.MOVEMENT)
public final class TerrainCheat extends Cheat
{
    @CheatInstance
    public static TerrainCheat INSTANCE;

    public static final float NCP_ICE_MAX = 0.391f;

    private final Setting<Boolean> iceSetting = builder("Ice", false)
            .setDescription("If to reduce friction on ice")
            .build();
    private final Setting<Double> stairSpeedMultiplierSetting = numberBuilder("Stair Speed Multi", 1.0)
            .setMin(1.0)
            .setMax(5.0)
            .setScale(0.1)
            .setDescription("The multiplier speed to go on stairs")
            .build();

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
