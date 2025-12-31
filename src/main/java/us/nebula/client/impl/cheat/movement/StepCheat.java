package us.nebula.client.impl.cheat.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.impl.event.player.EventStep;

/**
 * @author xgraza
 * @since 04/04/25
 */
@CheatManifest(name = "Step",
        description = "Send extra packets to step up blocks without jumping",
        category = CheatCategory.MOVEMENT)
public final class StepCheat extends Cheat
{
    private static final double[] STEP_PACKET_VALUES = { 0.42f, 0.753f, 1.0f };

    private boolean timer;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        if (MC.thePlayer != null)
        {
            MC.thePlayer.stepHeight = 0.5f;
        }
        if (timer)
        {
            MC.timer.timerSpeed = 1.0f;
        }
        timer = false;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        MC.thePlayer.stepHeight = 1.0f;
        if (timer && MC.thePlayer.onGround)
        {
            timer = false;
            MC.timer.timerSpeed = 1.0f;
        }
    };

    @Subscribe
    private final EventListener<EventStep> stepEventListener = event ->
    {
        if (!MC.thePlayer.onGround)
        {
            return;
        }

        final float offset = (float) (MC.thePlayer.boundingBox.minY
                        - (MC.thePlayer.posY - MC.thePlayer.yOffset));
        if (offset < 0.6 || offset > 1.0)
        {
            return;
        }

        final double minY = MC.thePlayer.boundingBox.minY - 1.0;
        final double stance = minY + (double) MC.thePlayer.yOffset - MC.thePlayer.ySize;

        timer = true;
        MC.timer.timerSpeed = 1.0f / (STEP_PACKET_VALUES.length + 1);
        for (double packetHeight : STEP_PACKET_VALUES)
        {
            //packetHeight *= offset;
            MC.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                    MC.thePlayer.posX,
                    minY + packetHeight,
                    stance + packetHeight,
                    MC.thePlayer.posZ,
                    false));
        }
    };
}
