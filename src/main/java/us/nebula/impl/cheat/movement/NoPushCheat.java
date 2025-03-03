package us.nebula.impl.cheat.movement;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.impl.event.player.EventPushFromBlocks;
import us.nebula.impl.event.player.EventPushWater;

/**
 * @author xgraza
 * @since 03/02/25
 */
@CheatManifest(name = "NoPush",
        description = "Prevents blocks/entities from pushing you",
        category = CheatCategory.MOVEMENT)
public final class NoPushCheat extends Cheat
{
    private final Setting<Boolean> blocksSetting = new Setting<>(
            "Blocks", false);
    private final Setting<Boolean> waterSetting = new Setting<>(
            "Water", false);

    @Subscribe
    private final EventListener<EventPushFromBlocks> pushFromBlocksEventListener = event ->
    {
        if (blocksSetting.getValue())
        {
            event.setCanceled(true);
        }
    };

    @Subscribe
    private final EventListener<EventPushWater> pushWaterEventListener = event ->
    {
        if (waterSetting.getValue() && event.getEntity().equals(MC.thePlayer))
        {
            event.setCanceled(true);
        }
    };
}
