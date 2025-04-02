package us.nebula.impl.cheat.combat;

import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import us.nebula.Nebula;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.manager.rotate.RotationConfirmation;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.impl.event.player.EventMoveUpdate;
import us.nebula.util.math.Timer;

import java.util.List;

/**
 * @author xgraza
 * @since 03/22/25
 */
@CheatManifest(name = "AutoPot", category = CheatCategory.COMBAT)
public final class AutoPotCheat extends Cheat implements RotationConfirmation
{
    private static final int AUTOPOT_ROTATION_PRIORITY = 100;
    private static final int PREDICT_TICKS = 4;

    private final Setting<Float> healthSetting = new Setting<>(
            "Health", 8.0f, 1.0f, 19.5f, 0.5f);
    private final Setting<Boolean> serverConfirmSetting = new Setting<>(
            "Server Confirm", false);

    private final Timer timer = new Timer();
    private int potSlot;
    private boolean await;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        if (potSlot != -1 && MC.thePlayer != null)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
        potSlot = -1;
        await = false;
    }

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (MC.thePlayer == null)
        {
            return;
        }
        if (event.getPacket() instanceof S1DPacketEntityEffect)
        {
            final S1DPacketEntityEffect packet = event.getPacket();
            if (packet.getEntityId() != MC.thePlayer.getEntityId() || !await)
            {
                return;
            }
            // TODO: should i check what kind of effect?
            await = false;
            potSlot = -1;
            timer.resetTime();
        }
    };

    @Subscribe
    private final EventListener<EventMoveUpdate> updateEventListener = event ->
    {
        if (!timer.hasElapsed(250L) || await)
        {
            return;
        }
        potSlot = getSlot();
        if (potSlot == -1)
        {
            return;
        }
        setPotAngles();
        timer.resetTime();
    };

    @Override
    public void onServerRotateConfirm(final float yaw, final float pitch)
    {
        if (potSlot == -1)
        {
            return;
        }
        Nebula.INSTANCE.getInventoryManager().setSlot(potSlot);
        MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                Nebula.INSTANCE.getInventoryManager().getStack()));
        MC.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation(MC.thePlayer, 1));
        Nebula.INSTANCE.getInventoryManager().syncSlot();

        timer.resetTime();
        if (serverConfirmSetting.getValue())
        {
            await = true;
            return;
        }

        // resets
        potSlot = -1;
    }

    private int getSlot()
    {
        hotbarSlotLookup:
        for (int i = 0; i < 9; ++i)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null
                    || !(itemStack.getItem() instanceof ItemPotion)
                    || !ItemPotion.isSplash(itemStack.getItemDamage()))
            {
                continue;
            }
            final ItemPotion itemPotion = (ItemPotion) itemStack.getItem();
            final List<PotionEffect> effectList = itemPotion.getEffects(itemStack);
            for (final PotionEffect potionEffect : effectList)
            {
                final Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                if (potion == Potion.heal && MC.thePlayer.getHealth() > healthSetting.getValue()
                        || MC.thePlayer.isPotionActive(potion)
                        || potion.isBadEffect())
                {
                    continue hotbarSlotLookup;
                }
            }
            // if we did not continue back to our main loop let's use it
            return i;
        }
        return -1;
    }

    public void setPotAngles()
    {
        final double predictedX = MC.thePlayer.posX + (MC.thePlayer.motionX * PREDICT_TICKS);
        final double predictedZ = MC.thePlayer.posZ + (MC.thePlayer.motionZ * PREDICT_TICKS);
        final double deltaX = Math.abs(predictedX - MC.thePlayer.posX);
        final double deltaZ = Math.abs(predictedZ - MC.thePlayer.posZ);
        final double dist = 1.0 - Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        final float pitch = (float) (90.0f * dist);
        float yaw = MC.thePlayer.rotationYaw;
        if (MC.thePlayer.moveForward < 0.0f)
        {
            yaw += 180.0f;
        }
        Nebula.INSTANCE.getRotationManager().spoofAndConfirm(yaw, pitch,
                AUTOPOT_ROTATION_PRIORITY, this);
    }
}
