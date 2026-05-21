package us.nebula.client.cheat.impl.movement;

import net.minecraft.block.BlockIce;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.player.PlayerUtil;
import us.nebula.client.util.value.Setting;
import us.nebula.client.cheat.impl.world.ScaffoldCheat;
import us.nebula.client.listener.event.network.EventPacket;
import us.nebula.client.listener.event.player.EventMove;
import us.nebula.client.listener.event.player.EventMoveUpdate;
import us.nebula.client.cheat.gui.component.cheat.value.EnumSettingComponent;
import us.nebula.client.util.player.MoveUtil;

/**
 * @author xgraza
 * @since 03/26/25
 */
@CheatManifest(name = "Speed",
        description = "Allows you to move faster",
        category = CheatCategory.MOVEMENT)
public final class SpeedCheat extends Cheat
{
    @CheatInstance
    public static SpeedCheat INSTANCE;

    public final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.STRAFE);
    public final Setting<Boolean> timerSetting = new Setting<>(
            "Use Timer", false)
            .setVisibility(() -> modeSetting.getValue() == Mode.STRAFE);
    public final Setting<Integer> advanceSetting = new Setting<>(
            "Advance", 1, 1, 10, 1)
            .setVisibility(() -> modeSetting.getValue() == Mode.TICK_ADVANCE);

    private double lastDistance, speed;
    private int lagTicks, stage;
    private boolean boostTick;

    @Override
    public void onDisable()
    {
        super.onDisable();
        lastDistance = 0.0;
        speed = 0.0;
        lagTicks = 0;
        MC.timer.timerSpeed = 1.0f;
        boostTick = false;
    }

    @Subscribe
    private final EventListener<EventMove> moveEventListener = event ->
    {
        if (--lagTicks > 0)
        {
            MC.timer.timerSpeed = 1.0f;
            return;
        }

        if (modeSetting.getValue() == Mode.STRAFE)
        {
            final boolean useTimer = timerSetting.getValue() && !ScaffoldCheat.INSTANCE.isToggled();

            if (!MoveUtil.isMoving())
            {
                MC.timer.timerSpeed = 1.0f;
                speed = 1.22 * MoveUtil.getBaseNcpSpeed(20) - 0.01;
                stage = 0;
            }

            double friction = 0.99;
            if (MC.theWorld.getBlock(PlayerUtil.getOrigin().down()) instanceof BlockIce)
            {
                friction = 1.55;
            }

            if (MoveUtil.isMoving() && MC.thePlayer.onGround)
            {
                stage = 1;
                MC.thePlayer.motionY = MoveUtil.getJumpHeight(0.3995f);
                event.setY(MC.thePlayer.motionY);
                speed *= boostTick ? 1.64 : 1.59;
                speed *= friction;
            } else
            {
                if (stage == 1)
                {
                    double deboost = boostTick ? 0.8 : 0.7;
                    if (friction > 0.99)
                    {
                        deboost -= 0.02;
                    }
                    final double diff = deboost * (speed - MoveUtil.getBaseNcpSpeed(4));
                    speed = lastDistance - diff;
                    stage = 2;
                } else if (stage == 2)
                {
                    double slowdown = boostTick ? 159 : 139;
                    if (friction > 0.99)
                    {
                        slowdown += 30;
                    }
                    speed -= speed / slowdown;
                    boostTick = !boostTick;
                }

                if (boostTick && useTimer)
                {
                    MC.timer.timerSpeed = 1.088f;
                } else
                {
                    MC.timer.timerSpeed = 1.0f;
                }
            }

            speed = Math.max(speed, MoveUtil.getBaseNcpSpeed(4));

            if (MoveUtil.isMoving())
            {
                MoveUtil.setSpeed(event, speed);
            } else
            {
                MC.timer.timerSpeed = 1.0f;
            }
        }
    };

    @Subscribe
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
    {
        final double diffX = MC.thePlayer.posX - MC.thePlayer.lastTickPosX;
        final double diffZ = MC.thePlayer.posZ - MC.thePlayer.lastTickPosZ;
        lastDistance = Math.sqrt(diffX * diffX + diffZ * diffZ);
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S08PacketPlayerPosLook)
        {
            MC.timer.timerSpeed = 1.0f;
            lagTicks = 8;
        }
    };

    @Override
    public String getMetadata()
    {
        return EnumSettingComponent.formatEnum(modeSetting.getValue());
    }

    @Override
    public boolean isActive()
    {
        return isToggled() && MoveUtil.isMoving();
    }

    public enum Mode
    {
        STRAFE, TICK_ADVANCE
    }
}
