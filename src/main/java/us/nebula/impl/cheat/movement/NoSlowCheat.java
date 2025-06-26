package us.nebula.impl.cheat.movement;

import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.cheat.combat.KillAuraCheat;
import us.nebula.impl.event.game.EventPostUpdate;
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
    @CheatInstance
    public static NoSlowCheat INSTANCE;

    private final Setting<Boolean> ncpBypassSetting = new Setting<>(
            "NCP Bypass", false);

    private boolean bypass;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        bypass = false;
    }

    @Subscribe
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
    {
        if (ncpBypassSetting.getValue() && isBlocking() && event.isOnGround())
        {
            bypass = true;
            MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                    5, 0, 0, 0, 255));
        }
    };

    @Subscribe
    private final EventListener<EventPostUpdate> postUpdateEventListener = event ->
    {
        if (bypass)
        {
            bypass = false;
            MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    -1, -1, -1, 255,
                    MC.thePlayer.getHeldItem(), 0.0F, 0.0F, 0.0F));
        }
    };

    @Subscribe
    private final EventListener<EventItemSlowdown> itemSlowdownEventListener = event ->
    {
        event.getInput().moveForward *= 5.0f;
        event.getInput().moveStrafe *= 5.0f;
    };

    private boolean isBlocking()
    {
        if (KillAuraCheat.INSTANCE.isToggled()
                && KillAuraCheat.INSTANCE.isAttacking())
        {
            return KillAuraCheat.INSTANCE.isBlocking();
        }
        return MC.thePlayer.isBlocking();
    }
}
