package us.nebula.client.cheat.impl.combat;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.src.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import us.nebula.client.Nebula;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.listener.event.network.EventPacket;
import us.nebula.client.listener.event.player.EventMoveUpdate;
import us.nebula.client.util.math.Timer;
import us.nebula.client.util.player.InventoryUtil;
import us.nebula.client.util.player.PlayerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xgraza
 * @since 03/22/25
 */
@CheatManifest(name = "AutoPot", category = CheatCategory.COMBAT)
public final class AutoPotCheat extends Cheat
{
    @CheatInstance
    public static AutoPotCheat INSTANCE;

    private static final List<Integer> ALLOWED_POTION_EFFECTS = Lists.newArrayList(
            Potion.heal.getId(),
            Potion.moveSpeed.getId(),
            Potion.damageBoost.getId(),
            Potion.digSpeed.getId(),
            Potion.resistance.getId(),
            Potion.fireResistance.getId(),
            Potion.waterBreathing.getId(),
            Potion.healthBoost.getId(),
            Potion.saturation.getId(),
            Potion.regeneration.getId());
    private static final int AUTOPOT_ROTATION_PRIORITY = 90;

    private final Setting<Float> healthSetting = new Setting<>(
            "Health", 10.0f, 1.0f, 19.5f, 0.5f);
    private final Setting<Boolean> raytraceCheckSetting = new Setting<>(
            "Raytrace Check", true);
    private final Setting<Boolean> ignoreIllegalSetting = new Setting<>(
            "Ignore Illegals", true);
    private final Setting<Boolean> neverRunOutSetting = new Setting<>(
            "Never Run Out", false);
    private final Setting<Boolean> prioritizeAttackingSetting = new Setting<>(
            "Prioritize Combat", false);

    private final List<Integer> expectedPotionEffects = new ArrayList<>();
    private final Timer potTimer = new Timer();
    private boolean rotated, thrown;
    private int lastPotionSlot = -1;

    @Override
    public void onDisable()
    {
        super.onDisable();
        lastPotionSlot = -1;
        rotated = false;
        thrown = false;
        expectedPotionEffects.clear();
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (thrown)
        {
            final long time = (long) (200 + Nebula.INSTANCE.getServerManager().getScaledLatency());
            if (potTimer.hasElapsed(time))
            {
                thrown = false;
                rotated = false;
                expectedPotionEffects.clear();
                lastPotionSlot = -1;
            }
            return;
        }

        // we cannot override AutoBed, as we need to spoof rotations to properly place the bed in the direction we want
        if (AutoBedCheat.INSTANCE.isActive())
        {
            return;
        }

        if ((prioritizeAttackingSetting.getValue() && (KillAuraCheat.INSTANCE.isAttacking() || AutoBedCheat.INSTANCE.isActive()))
                // allow us to finish the action we're doing
                || MC.thePlayer.getItemInUse() != null)
        {
            lastPotionSlot = -1;
            return;
        }

        if (isLowHealth() || lastPotionSlot == -1)
        {
            lastPotionSlot = getPotionSlot();
            if (lastPotionSlot == -1)
            {
                rotated = false;
                return;
            }
        }

        final float[] angles = getRotationAngles();
        if (angles == null)
        {
            rotated = false;
            return;
        }
        rotated = Nebula.INSTANCE.getRotationManager().spoof(angles[0], angles[1], AUTOPOT_ROTATION_PRIORITY);
        if (!rotated)
        {
            lastPotionSlot = -1;
        }
    };

    @Subscribe
    private final EventListener<EventMoveUpdate.Post> moveUpdatePostEventListener = event ->
    {
        if (lastPotionSlot == -1 || !rotated || thrown)
        {
            return;
        }
        Nebula.INSTANCE.getInventoryManager().setSlot(lastPotionSlot);
        MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(null));
        thrown = true;
        potTimer.resetTime();
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S1DPacketEntityEffect && thrown && !expectedPotionEffects.isEmpty())
        {
            final S1DPacketEntityEffect packet = event.getPacket();
            if (expectedPotionEffects.contains((int) packet.getPotionId()))
            {
                // confirmed, reset variables
                thrown = false;
                rotated = false;
                expectedPotionEffects.clear();
                lastPotionSlot = -1;
            }
        }
    };

    private float[] getRotationAngles()
    {
        final BlockPos origin = PlayerUtil.getOrigin();
        Vec3 predictedVector = Vec3.createVectorHelper(
                origin.getX() + 0.5,
                origin.getY() - 1.5,
                origin.getZ() + 0.5).addVector(
                MC.thePlayer.motionX * 8,
                MC.thePlayer.motionY * 2.5,
                MC.thePlayer.motionZ * 8);
        double distance = 1.0 / (MC.thePlayer.getDistanceSq(
                predictedVector.xCoord,
                predictedVector.yCoord,
                predictedVector.zCoord));

        final float[] angles = new float[]{ MC.thePlayer.rotationYaw, (float) ((1 - distance) * 90.0f) };

        if (!raytraceCheckSetting.getValue())
        {
            return angles;
        }

        // alfheim giant strength pot incident...
        final MovingObjectPosition result = MC.thePlayer.rayTrace(
                Nebula.INSTANCE.getRotationManager().getLook(angles[0], angles[1]),
                4.5, 1.0f);
        return (result == null || result.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) ? null : angles;
    }

    private int getPotionSlot()
    {
        int slot = InventoryUtil.INVALID_SLOT;
        List<PotionEffect> effectList = null;
        float scale = 0.0f;

        for (int i = 0; i < InventoryUtil.HOTBAR_SIZE; ++i)
        {
            final ItemStack stack = MC.thePlayer.inventory.getStackInSlot(i);
            if (stack == null || !(stack.getItem() instanceof ItemPotion) || !ItemPotion.isSplash(stack.getItemDamage()))
            {
                continue;
            }
            final ItemPotion itemPotion = (ItemPotion) stack.getItem();
            final List<PotionEffect> potionEffects = itemPotion.getEffects(stack);

            float score = 0.0f;
            for (final PotionEffect effect : potionEffects)
            {
                final int id = effect.getPotionID();
                if (!ALLOWED_POTION_EFFECTS.contains(id)
                        // Alfheim has a patch where if the potion has a greater amplifier than normally allowed, it will not apply :(
                        || (ignoreIllegalSetting.getValue() && isPotionIllegal(effect)))
                {
                    score = -1.0f;
                    break;
                }

                if (id == Potion.heal.getId())
                {
                    // if we are desperately low, ignore finding another effect and focus on healing
                    if (isLowHealth())
                    {
                        return i;
                    } else
                    {
                        score = -1.0f;
                        break;
                    }
                }

                final int amplifier = isPotionApplied(id)
                        ? MC.thePlayer.getActivePotionEffect(id).getAmplifier()
                        : -1;
                if (amplifier < effect.getAmplifier())
                {
                    score += effect.getAmplifier() + 1;
                }
            }

            if (score <= 0.0f)
            {
                continue;
            }

            if (slot == InventoryUtil.INVALID_SLOT || score > scale)
            {
                effectList = potionEffects;
                slot = i;
                scale = score;
            }
        }

        if (slot != -1)
        {
            expectedPotionEffects.clear();
            for (final PotionEffect effect : effectList)
            {
                expectedPotionEffects.add(effect.getPotionID());
            }
        }

        return slot;
    }

    private boolean isLowHealth()
    {
        return MC.thePlayer.getHealth() <= healthSetting.getValue();
    }

    private boolean isPotionApplied(final int id)
    {
        if (!MC.thePlayer.isPotionActive(id))
        {
            return false;
        }
        if (neverRunOutSetting.getValue())
        {
            final PotionEffect effect = MC.thePlayer.getActivePotionEffect(id);
            return effect != null && effect.getDuration() >= 200;
        }
        return true;
    }

    private boolean isPotionIllegal(final PotionEffect effect)
    {
        if (effect.getAmplifier() < 0)
        {
            return true;
        }
        final Potion potion = Potion.potionTypes[effect.getPotionID()];
        if (potion == Potion.fireResistance
                || potion == Potion.weakness
                || potion == Potion.moveSlowdown
                || potion == Potion.waterBreathing
                || potion == Potion.nightVision)
        {
            return effect.getAmplifier() != 0;
        }
        return effect.getAmplifier() > 1;
    }

    @Override
    public boolean isActive()
    {
        return super.isActive() && (thrown || lastPotionSlot != -1);
    }
}
