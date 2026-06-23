package ez.nebula.client.impl.module.movement;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.world.EventBlockSlipperiness;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;

/**
 * @author xgraza
 * @since 6/23/26
 */
@ModuleManifest(name = "IceSpeed",
        description = "Allows you to move faster on ice",
        category = ModuleCategory.MOVEMENT)
public final class IceSpeedModule extends Module
{
    public static final float NCP_ICE_MAX = 0.391f;

    public final Setting<Boolean> packedIceSetting = builder("Packed Ice", false)
            .setDescription("If to allow you to go faster on packed ice")
            .build();

    @Subscribe
    private final EventListener<EventBlockSlipperiness> blockSlipperinessEventListener = event ->
    {
        if (!event.getEntity().equals(MC.thePlayer)
                || !(event.getBlock() instanceof BlockIce
                    || event.getBlock() instanceof BlockPackedIce))
        {
            return;
        }
        if (!packedIceSetting.getValue() && event.getBlock() instanceof BlockPackedIce)
        {
            return;
        }
        event.setSlipperiness(NCP_ICE_MAX);
    };
}
