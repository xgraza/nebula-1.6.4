package us.nebula.impl.cheat.movement;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import us.nebula.ClientSettings;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.impl.event.player.EventMove;
import us.nebula.util.player.ChatUtil;
import us.nebula.util.player.MoveUtil;

/**
 * @author xgraza
 * @since 03/17/25
 */
@CheatManifest(name = "Speed",
        description = "Allows you to move faster",
        category = CheatCategory.MOVEMENT)
public final class SpeedCheat extends Cheat
{
    private final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.STRAFE);
    private final Setting<Boolean> handleLagbacksSetting = new Setting<>(
            "Handle Lagbacks", true);

    private double speed, distanceTraveled;
    private int strafeStage, lagbackTimer;
    private boolean boost;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        speed = 0.0;
        distanceTraveled = 0.0;
        strafeStage = 0;
        lagbackTimer = 0;
        boost = false;
        MC.timer.timerSpeed = 1.0f;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        distanceTraveled = MoveUtil.getPlayerMoveDistance();

        if (modeSetting.getValue() == Mode.Y_PORT && MoveUtil.isMoving())
        {
            if (--lagbackTimer > 0)
            {
                return;
            }
            MC.thePlayer.setSprinting(true);
            speed = 1.2 * MoveUtil.getBaseNcpSpeed(0) - 0.04;
            MC.thePlayer.setSprinting(true);
            if (MC.thePlayer.onGround)
            {
                MC.thePlayer.motionY = 0.42f;
                speed *= boost ? 1.62 : 1.526;
                strafeStage = 0;
            } else
            {
                MC.thePlayer.motionY = -1;
                boost = !boost;
            }
        }
    };

    @Subscribe
    private final EventListener<EventMove> moveEventListener = event ->
    {
        if (--lagbackTimer > 0)
        {
            return;
        }

        switch (modeSetting.getValue())
        {
            case STRAFE:
            {
                if (speed <= 0.0)
                {
                    speed = 1.38 * MoveUtil.getBaseNcpSpeed(10) - 0.1;
                }

                if (MC.thePlayer.onGround && MoveUtil.isMoving())
                {
                    strafeStage = 0;
                }

                switch (strafeStage++)
                {
                    case 0:
                    {
                        if (MC.thePlayer.onGround && MoveUtil.isMoving())
                        {
                            MC.thePlayer.motionY = MoveUtil.getJumpHeight(0.3995);
                            event.setY(MC.thePlayer.motionY);
                            speed *= boost ? 1.624 : 1.543;
                        }
                        break;
                    }
                    case 1:
                    {
                        final double modifier = boost ? 0.72 : 0.66;
                        speed = distanceTraveled - (modifier * (distanceTraveled - MoveUtil.getBaseNcpSpeed(10)));
                        boost = !boost;
                        break;
                    }
                    default:
                    {
                        speed -= speed / 159.0;
                    }
                }
                break;
            }
            case Y_PORT:
            {
                break;
            }
        }

        if (MoveUtil.isMoving())
        {
            MoveUtil.setSpeed(event, speed);
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S08PacketPlayerPosLook)
        {
            if (!handleLagbacksSetting.getValue())
            {
                return;
            }
            if (ClientSettings.VERBOSE_LOGGING)
            {
                ChatUtil.send("Setting lagback ticks to 10...");
            }
            lagbackTimer = 10;
        }
    };

    private enum Mode
    {
        STRAFE, Y_PORT
    }
}
