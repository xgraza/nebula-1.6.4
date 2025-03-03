package us.nebula.impl.cheat.movement;

import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.player.EventItemSlowdown;
import us.nebula.impl.event.player.EventMoveUpdate;

/**
 * @author xgraza
 * @since 03/02/25
 */
@CheatManifest(name = "NoSlow",
        description = "Negates slowdowns when eating",
        category = CheatCategory.MOVEMENT)
public final class NoSlowCheat extends Cheat
{
    private final Setting<Boolean> ncpBypassSetting = new Setting<>(
            "NCP Bypass", false);

    @Subscribe
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
    {
        if (ncpBypassSetting.getValue() && MC.thePlayer.isBlocking())
        {
            if (MC.thePlayer.ticksExisted % 2 == 0)
            {
                MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                        5, 0, 0, 0, 255));
            } else
            {
                MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                        MC.thePlayer.getHeldItem()));
            }
        }
    };

    @Subscribe
    private final EventListener<EventItemSlowdown> itemSlowdownEventListener = event ->
    {
        event.getInput().moveForward *= 5.0f;
        event.getInput().moveStrafe *= 5.0f;
    };
}
