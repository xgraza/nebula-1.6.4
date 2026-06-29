package ez.nebula.client.impl.module.movement;

import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.input.EventUpdateRiding;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.NumberSetting;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;

/**
 * @author xgraza
 * @since 6/5/26
 */
@DebugFeature
@ModuleManifest(name = "EntitySpeed",
        description = "Attempts to make moving on an entity faster",
        category = ModuleCategory.MOVEMENT)
public final class EntitySpeedModule extends Module
{
    private final NumberSetting<Double> speedSetting = numberBuilder("Speed", 0.2)
            .setMin(0.1)
            .setMax(5.0)
            .setScale(0.1)
            .setDescription("How fast to go on an entity")
            .build();

    @Subscribe
    private final EventListener<EventUpdateRiding> updateRidingEventListener = event ->
    {
        MC.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput(
                MC.thePlayer.moveStrafing,
                MC.thePlayer.moveForward,
                MC.thePlayer.movementInput.jump,
                MC.thePlayer.movementInput.sneak));

        MC.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(
                MC.thePlayer.motionX, -999, -999, MC.thePlayer.motionZ,
                MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, MC.thePlayer.onGround));
    };
}
