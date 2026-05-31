package us.nebula.client.cheat.impl.movement;

import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.cheat.impl.combat.KillAuraCheat;
import us.nebula.client.listener.event.game.EventPostUpdate;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.listener.event.player.*;
import us.nebula.client.setting.Setting;

/**
 * @author xgraza
 * @since 03/02/25
 */
@CheatManifest(name = "NoSlow",
        description = "Negates vanilla slowdowns for eating, blocking, webs, blocks, and water",
        category = CheatCategory.MOVEMENT)
public final class NoSlowCheat extends Cheat
{
    private final Setting<Mode> modeSetting = enumBuilder("Mode", Mode.VANILLA)
            .setDescription("The mode for preventing slowdowns")
            .build();
    private final Setting<Boolean> websSetting = builder("Webs", false)
            .setDescription("If to prevent slowdowns through webs")
            .build();
    private final Setting<Boolean> blocksSetting = builder("Blocks", false)
            .setDescription("If to prevent blocks from slowing you down")
            .build();
    private final Setting<Boolean> waterSetting = builder("Water", false)
            .setDescription("If to prevent water slowdowns")
            .build();

    private boolean bypass, inWeb;

    @Override
    public void onDisable()
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
