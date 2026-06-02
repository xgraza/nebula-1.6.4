package ez.nebula.client.impl.module.movement;

import ez.nebula.client.api.manager.module.Module;
import net.minecraft.block.BlockStairs;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.player.EventMove;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.minecraft.player.PlayerUtil;

/**
 * @author xgraza
 * @since 12/30/25
 */
@ModuleManifest(name = "Terrain",
        description = "Changes how your player behaves on specific terrain (i.e. ice, stairs)",
        category = ModuleCategory.MOVEMENT)
public final class TerrainModule extends Module
{
    @ModuleInstance
    public static TerrainModule INSTANCE;

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
