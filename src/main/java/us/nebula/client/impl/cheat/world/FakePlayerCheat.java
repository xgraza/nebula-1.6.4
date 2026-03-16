package us.nebula.client.impl.cheat.world;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.IEventPriorities;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatInstance;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.impl.event.network.EventPacket;
import us.nebula.client.util.math.MathUtil;
import us.nebula.client.util.player.ChatUtil;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author xgraza
 * @since 06/06/25
 */
@CheatManifest(name = "FakePlayer",
        description = "Spawns a fake player to test things on",
        category = CheatCategory.WORLD)
public final class FakePlayerCheat extends Cheat
{
    @CheatInstance
    public static FakePlayerCheat INSTANCE;

    private static final List<String> FAKE_USERNAMES = Lists.newArrayList(
            "Aestheticall", "epearl", "hometea", "iWoodz", "EstrogenInjector");
    private static final int FAKE_ENTITY_ID = -1337420;

    private final Setting<Boolean> takeDamageSetting = new Setting<>(
            "Take Damage", false);
    private final Setting<Boolean> gapChugSetting = new Setting<>(
            "Gap Chug", false, takeDamageSetting::getValue);
    private final Setting<Boolean> moveSetting = new Setting<>(
            "Move", false);
    private final Setting<Boolean> recordSetting = new Setting<>(
            "Start Recording", false)
            .setVisibility(moveSetting::getValue);

    private final Queue<Movement> fakePlayerMovement = new ConcurrentLinkedQueue<>();
    private Movement lastRecordedMovement;
    private boolean shownRecordScreen;

    private EntityOtherPlayerMP fakePlayerEntity;

    @Override
    protected void onEnable()
    {
        super.onEnable();
        if (MC.thePlayer == null || MC.theWorld == null)
        {
            setToggled(false);
        }
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        if (MC.theWorld != null)
        {
            despawnFP();
        }

        fakePlayerMovement.clear();
        lastRecordedMovement = null;
        shownRecordScreen = false;
        recordSetting.setValue(false);
    }

    @Override
    public String getMetadata()
    {
        if (recordSetting.getValue())
        {
            return "Recording";
        }
        if (!fakePlayerMovement.isEmpty() && moveSetting.getValue())
        {
            return "Moving (" + fakePlayerMovement.size() + ")";
        }
        return super.getMetadata();
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (MC.thePlayer.ticksExisted < 20)
        {
            return;
        }

        if (recordSetting.getValue())
        {
            if (!shownRecordScreen)
            {
                if (MC.currentScreen != null)
                {
                    MC.displayGuiScreen(null);
                }

                lastRecordedMovement = null;
                shownRecordScreen = true;
                fakePlayerMovement.clear();

                ChatUtil.send("Begin moving now! Sneak to end recording");
            }

            if (MC.gameSettings.keyBindSneak.pressed)
            {
                ChatUtil.send("Finished recording(%s)!", fakePlayerMovement.size());
                lastRecordedMovement = null;
                recordSetting.setValue(false);
                return;
            }

            final Movement movement = new Movement(MC.thePlayer.posX, MC.thePlayer.boundingBox.minY, MC.thePlayer.posZ,
                    MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch);
            if (Objects.equals(movement, lastRecordedMovement))
            {
                return;
            }
            lastRecordedMovement = movement;
            fakePlayerMovement.add(movement);

            return;
        }

        if (isFakePlayerInvalidated())
        {
            fakePlayerMovement.clear();
            if (fakePlayerEntity != null)
            {
                MC.theWorld.removeEntityFromWorld(FAKE_ENTITY_ID);
                MC.theWorld.removePlayerEntityDangerously(fakePlayerEntity);
            }
            if (MC.thePlayer.ticksExisted < 20)
            {
                return;
            }
            fakePlayerEntity = createFakePlayer();
            MC.theWorld.addEntityToWorld(FAKE_ENTITY_ID, fakePlayerEntity);
        }

        if (moveSetting.getValue() && fakePlayerEntity != null && !fakePlayerMovement.isEmpty())
        {
            final Movement movement = fakePlayerMovement.poll();
            if (movement == null)
            {
                // should never be null, as we add this one to the end of the stack but...
                return;
            }

            fakePlayerEntity.setPositionAndRotation(movement.x, movement.y, movement.z, movement.yaw, movement.pitch);
            fakePlayerEntity.rotationYawHead = movement.yaw;
            fakePlayerEntity.renderPitch = movement.pitch;

            fakePlayerMovement.add(movement);
        }
    };

    @Subscribe(priority = IEventPriorities.LOW)
    private final EventListener<EventPacket.Outbound> outboundEventListener = event ->
    {
        if (event.getPacket() instanceof C02PacketUseEntity)
        {
            final C02PacketUseEntity packet = event.getPacket();
            final Entity entity = packet.func_149564_a(MC.theWorld);
            if (entity != null && entity.getEntityId() == FAKE_ENTITY_ID)
            {
                event.cancel();
            }
        }
    };

    private void despawnFP()
    {
        if (fakePlayerEntity == null)
        {
            return;
        }
        MC.theWorld.removeEntityFromWorld(FAKE_ENTITY_ID);
        MC.theWorld.removePlayerEntityDangerously(fakePlayerEntity);
        fakePlayerEntity = null;
    }

    public void critFake()
    {
        if (isToggled() && fakePlayerEntity != null)
        {
            MC.thePlayer.onCriticalHit(fakePlayerEntity);
        }
    }

    private EntityOtherPlayerMP createFakePlayer()
    {
        final EntityPlayerSP local = MC.thePlayer;
        final GameProfile gameProfile = new GameProfile(
                "", FAKE_USERNAMES.get(MathUtil.random(0, FAKE_USERNAMES.size() - 1)));
        return new EntityOtherPlayerMP(MC.theWorld, gameProfile)
        {
            {
                setEntityId(FAKE_ENTITY_ID);
                dimension = local.dimension;
                setLocationAndAngles(
                        local.posX, local.boundingBox.minY, local.posZ,
                        local.rotationYaw, local.rotationPitch);
                inventory.copyInventory(local.inventory);
            }

            @Override
            public void onLivingUpdate()
            {
                super.onLivingUpdate();

                if (getHealth() == 0.0f)
                {
                    setHealth(20.0f);
                }

                if (gapChugSetting.getValue() && local.ticksExisted % 120 == 0)
                {
                    inventory.mainInventory[0] = new ItemStack(Items.golden_apple);

                    setHealth(20.0f);
                    setAbsorptionAmount(4.0f);
                    addPotionEffect(new PotionEffect(Potion.absorption.id, 2400, 0));
                }
            }

            @Override
            public boolean attackEntityFrom(final DamageSource src, float damage)
            {
                if (!takeDamageSetting.getValue())
                {
                    return false;
                }

                entityAge = 0;

                if (getHealth() <= 0.0f)
                {
                    return false;
                }

                if (src.isDifficultyScaled())
                {
                    switch (worldObj.difficultySetting)
                    {
                        case PEACEFUL:
                        {
                            damage = 0.0f;
                            break;
                        }
                        case EASY:
                        {
                            damage = damage / 2.0f + 1.0f;
                            break;
                        }
                        case HARD:
                        {
                            damage = damage * 3.0f / 2.0f;
                            break;
                        }
                    }
                }

                if (damage <= 0.0f)
                {
                    return false;
                }

                limbSwingAmount = 1.5f;

                boolean dealtFullDamage = true;
                if (hurtResistantTime > maxHurtResistantTime / 2.0f)
                {
                    if (damage <= lastDamage)
                    {
                        return false;
                    }

                    damageEntity(src, damage - lastDamage);
                    lastDamage = damage;
                    dealtFullDamage = false;
                } else
                {
                    lastDamage = damage;
                    prevHealth = getHealth();
                    hurtResistantTime = maxHurtResistantTime;
                    damageEntity(src, damage);
                    hurtTime = maxHurtTime = 10;
                }

                if (getHealth() <= 0.0f)
                {
                    if (dealtFullDamage)
                    {
                        playSound(getDeathSound(), getSoundVolume(), getSoundPitch());
                    }
                    setHealth(20.0f);
                } else
                {
                    if (dealtFullDamage)
                    {
                        playSound(getHurtSound(), getSoundVolume(), getSoundPitch());
                    }
                }

                return true;
            }
        };
    }

    private boolean isFakePlayerInvalidated()
    {
        return fakePlayerEntity == null || fakePlayerEntity.isDead || fakePlayerEntity.dimension != MC.thePlayer.dimension;
    }

    private static final class Movement
    {
        private final double x, y, z;
        private final float yaw, pitch;

        public Movement(double x, double y, double z, float yaw, float pitch)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }

        @Override
        public int hashCode()
        {
            return (int) ((y + z * 31) * 31 + x + (yaw + pitch * 31));
        }
    }
}
