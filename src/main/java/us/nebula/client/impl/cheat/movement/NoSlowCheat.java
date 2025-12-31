package us.nebula.client.impl.cheat.movement;

import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.cheat.combat.KillAuraCheat;
import us.nebula.client.impl.event.game.EventPostUpdate;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.impl.event.player.*;

/**
 * @author xgraza
 * @since 03/02/25
 */
@CheatManifest(name = "NoSlow",
        description = "Negates vanilla slowdowns for eating, blocking, webs, blocks, and water",
        category = CheatCategory.MOVEMENT)
public final class NoSlowCheat extends Cheat
{
    private final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.VANILLA);
    private final Setting<Boolean> websSetting = new Setting<>(
            "Webs", false);
    private final Setting<Boolean> blocksSetting = new Setting<>(
            "Blocks", false);
    private final Setting<Boolean> waterSetting = new Setting<>(
            "Water", false);

    private boolean bypass, inWeb;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        bypass = false;
        inWeb = false;
    }

    @Subscribe
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
    {
        if (modeSetting.getValue() == Mode.NCP && isBlocking() && event.isOnGround())
        {
            bypass = true;
            MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                    5, 0, 0, 0, 255));
        }
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (websSetting.getValue())
        {
            inWeb = MC.thePlayer.isInWeb;
            MC.thePlayer.isInWeb = false;
        }
    };

    @Subscribe
    private final EventListener<EventMove> moveEventListener = event ->
    {
        if (inWeb)
        {
            // Entity#moveEntity
            event.setY(event.getY() * 0.05000000074505806D);
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

    private boolean isBlocking()
    {
        if (KillAuraCheat.INSTANCE.isToggled()
                && KillAuraCheat.INSTANCE.isAttacking())
        {
            return KillAuraCheat.INSTANCE.isBlocking();
        }
        return MC.thePlayer.isBlocking();
    }

    private enum Mode
    {
        VANILLA,
        NCP
                {
                    @Override public String toString()
                    {
                        return "NCP";
                    }
                }
    }
}
