package us.nebula.impl.cheat.world;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.util.math.MathUtil;

import java.util.List;

/**
 * @author xgraza
 * @since 06/06/25
 */
@CheatManifest(name = "FakePlayer",
        description = "Spawns a fake player to test things on",
        category = CheatCategory.WORLD)
public final class FakePlayerCheat extends Cheat
{
    private static final List<String> FAKE_USERNAMES = Lists.newArrayList(
            "Aestheticall", "epearl", "hometea", "iWoodz", "EstrogenInjector");

    private static final int FAKE_ENTITY_ID = -1337420;

    private final Setting<Boolean> takeDamageSetting = new Setting<>(
            "Take Damage", false);
    private final Setting<Boolean> gapChugSetting = new Setting<>(
            "Gap Chug", false, takeDamageSetting::getValue);

    private EntityOtherPlayerMP fakePlayerEntity;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        if (MC.theWorld != null && fakePlayerEntity != null)
        {
            MC.theWorld.removeEntityFromWorld(FAKE_ENTITY_ID);
            MC.theWorld.removePlayerEntityDangerously(fakePlayerEntity);
        }
        fakePlayerEntity = null;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (isFakePlayerInvalidated())
        {
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
    };

    @Subscribe
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

                if (gapChugSetting.getValue() && local.ticksExisted % 40 == 0)
                {
                    setHealth(20.0f);
                    setAbsorptionAmount(4.0f);
                    addPotionEffect(new PotionEffect(Potion.absorption.id, 2400, 0));
                }
            }

            @Override
            public boolean attackEntityFrom(final DamageSource src, float damage)
            {
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
        return fakePlayerEntity == null
                //|| MC.theWorld.getEntityByID(FAKE_ENTITY_ID) == null
                || fakePlayerEntity.isDead
                || fakePlayerEntity.dimension != MC.thePlayer.dimension;
    }
}
