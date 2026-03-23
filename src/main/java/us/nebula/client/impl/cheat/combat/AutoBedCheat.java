package us.nebula.client.impl.cheat.combat;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBed;
import net.minecraft.potion.Potion;
import net.minecraft.src.BlockPos;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;
import us.nebula.client.Nebula;
import us.nebula.client.api.interaction.InteractionManager;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.cheat.player.FreecamCheat;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.impl.event.render.EventRender3D;
import us.nebula.client.util.player.InventoryUtil;
import us.nebula.client.util.player.PlayerUtil;
import us.nebula.client.util.render.RenderUtil;
import us.nebula.client.util.world.BlockInfo;
import us.nebula.client.util.world.BlockUtil;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author xgraza
 * @since 06/06/25
 */
@CheatManifest(name = "AutoBed",
        description = "Automatically places and breaks beds around an enemy",
        category = CheatCategory.COMBAT)
public final class AutoBedCheat extends Cheat
{
    private static final int AUTO_BED_ROTATION_PRIORITY = 100;
    private static final double BED_EXPLOSION_SIZE = 5.0;
    private static final float BED_EXPLOSION_STRENGTH = 10.0f;

    private final Setting<Float> rangeSetting = new Setting<>(
            "Range", 4.2f, 1.0f, 6.0f, 0.1f);
    private final Setting<Integer> yRangeSetting = new Setting<>(
            "Y-Range", 1, 1, 5, 1);
    private final Setting<Boolean> extinguishFireSetting = new Setting<>(
            "Extinguish Fire", true);

    private final Setting<Float> minDamageSetting = new Setting<>(
            "Min Damage", 6.0f, 1.0f, 19.5f, 0.1f);
    private final Setting<Boolean> averageDamageSetting = new Setting<>(
            "Average Damage", true);

    private final Setting<Boolean> suicideSetting = new Setting<>(
            "Suicide", false);
    private final Setting<Float> lethalHealthSetting = new Setting<>(
            "Lethal Health", 12.0f, 2.0f, 19.5f, 0.1f,
            () -> !suicideSetting.getValue());
    private final Setting<Float> lethalMultiplierSetting = new Setting<>(
            "Lethal Multiplier", 1.2f, 1.0f, 2.0f, 0.1f);
    private final Setting<Float> swapPenaltySetting = new Setting<>(
            "Swap Penalty", 2.0f, 0.5f, 12.0f, 0.5f);

    private final Setting<Boolean> renderSetting = new Setting<>(
            "Render", true);

    private final BedBlockInfo blockInfo = new BedBlockInfo(null, null);
    private EntityPlayer target;

    @Override
    public void onDisable()
    {
        super.onDisable();
        target = null;
        blockInfo.invalidate();
        if (MC.thePlayer != null)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
    }

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (blockInfo.getPos() == null || target == null || !renderSetting.getValue())
        {
            return;
        }
        RenderUtil.filledBox3D(new AxisAlignedBB(blockInfo.getPos())
                        .addCoord(blockInfo.getFacing().getFrontOffsetX(),
                                blockInfo.getFacing().getFrontOffsetY(),
                                blockInfo.getFacing().getFrontOffsetZ()),
                0, 0x80FF0000);
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (isInOverworld())
        {
            target = null;
            blockInfo.invalidate();
            return;
        }
        if (!isValidEntity(target))
        {
            target = getPlayerInRange();
            return;
        }
        final int bedSlot = InventoryUtil.getHotbarItem(ItemBed.class);
        if (bedSlot == -1)
        {
            return;
        }
        calculatePlacePosition();
        if (blockInfo.getPos() == null || blockInfo.getFacing() == null)
        {
            return;
        }

        if (extinguishFireSetting.getValue())
        {
            final BlockPos pos1 = blockInfo.getPos();
            final BlockPos pos2 = blockInfo.getPos().offset(blockInfo.getFacing());

            if (BlockUtil.isFire(pos1))
            {
                MC.playerController.clickBlock(pos1.getX(), pos1.getY(), pos1.getZ(), EnumFacing.UP.order_a);
                MC.thePlayer.swingItem();
            }

            if (BlockUtil.isFire(pos2))
            {
                MC.playerController.clickBlock(pos2.getX(), pos2.getY(), pos2.getZ(), EnumFacing.UP.order_a);
                MC.thePlayer.swingItem();
            }
        }

        Nebula.INSTANCE.getInventoryManager().setSlot(bedSlot);
        Nebula.INSTANCE.getRotationManager().spoof(
                BlockUtil.getHorizontalFacing(blockInfo.getFacing()) * 90.0f,
                0.0f, AUTO_BED_ROTATION_PRIORITY);
        if (InteractionManager.INSTANCE.rightClickBlock(blockInfo.getPos().down(), EnumFacing.UP))
        {
            InteractionManager.INSTANCE.rightClickBlock(blockInfo.getPos(), EnumFacing.UP);
        }
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    };

    private void calculatePlacePosition()
    {
        if (isInOverworld())
        {
            blockInfo.invalidate();
            return;
        }
        final BedBlockInfo info = new BedBlockInfo(blockInfo);
        final BlockPos origin = PlayerUtil.getOrigin(target);
        final Queue<BlockInfo> placements = getPlacements(origin);
        if (placements.isEmpty())
        {
            blockInfo.invalidate();
            return;
        }

        while (!placements.isEmpty())
        {
            final BlockInfo bedPos = placements.poll();
            if (bedPos == null)
            {
                break;
            }

            final BlockPos bedOrigin = bedPos.getPos();
            final BlockPos bedNeighbor = bedPos.getPos().offset(bedPos.getFacing());

            final float targetDmg1 = calcDamage(target, bedOrigin);
            final float targetDmg2 = calcDamage(target, bedNeighbor);
            if (targetDmg1 < minDamageSetting.getValue() && targetDmg2 < minDamageSetting.getValue())
            {
                continue;
            }

            // average both block positions with both local & target damage...
            float localDamage = 0.0f;
            if (!suicideSetting.getValue() && !MC.thePlayer.capabilities.isCreativeMode)
            {
                final float lethal = lethalHealthSetting.getValue();
                final float localDmg1 = calcDamage(MC.thePlayer, bedOrigin);
                final float localDmg2 = calcDamage(MC.thePlayer, bedNeighbor);
                if (localDmg1 >= lethal || localDmg2 >= lethal)
                {
                    continue;
                }
                if (averageDamageSetting.getValue())
                {
                    localDamage = (localDmg1 + localDmg2) * 0.5f;
                } else
                {
                    localDamage = localDmg1;
                }
                if (localDamage * lethalMultiplierSetting.getValue() >= lethal)
                {
                    continue;
                }
            }
            final float targetDmg = (targetDmg1 + targetDmg1) * 0.5f;
            if (info.getTargetDamage() < targetDmg)
            {
                // if the switch target doesn't do much more damage & does more damage to us...
                if (Math.abs(info.getTargetDamage() - targetDmg) <= swapPenaltySetting.getValue()
                        && localDamage > info.getLocalDamage())
                {
                    continue;
                }
                info.setPos(bedPos.getPos());
                info.setFacing(bedPos.getFacing());
                info.setTargetDamage(targetDmg);
                info.setLocalDamage(localDamage);
            }
        }
        blockInfo.copy(info);
    }

    private Queue<BlockInfo> getPlacements(final BlockPos pos)
    {
        final Queue<BlockInfo> placements = new ConcurrentLinkedQueue<>();
        final Set<BlockPos> excluded = new HashSet<>();
        final int range = rangeSetting.getValue().intValue();
        for (int y = 0; y <= yRangeSetting.getValue(); ++y)
        {
            for (int x = -range; x <= range; ++x)
            {
                for (int z = -range; z <= range; ++z)
                {
                    final BlockPos neighbor = pos.add(x, y, z);
                    final EnumFacing face = getBedPlaceDirection(neighbor);
                    if (face != null && excluded.add(neighbor.offset(face)))
                    {
                        placements.add(new BlockInfo(neighbor, face));
                    }
                }
            }
        }
        return placements;
    }

    private EnumFacing getBedPlaceDirection(final BlockPos pos)
    {
        if (BlockUtil.isNotAir(pos) || BlockUtil.isReplaceable(pos.down()))
        {
            return null;
        }
        for (final EnumFacing facing : EnumFacing.values())
        {
            final BlockPos n = pos.offset(facing);
            if (BlockUtil.isNotAir(n) || BlockUtil.isReplaceable(n.down()))
            {
                continue;
            }
            return facing;
        }
        return null;
    }

    private EntityPlayer getPlayerInRange()
    {
        return (EntityPlayer) MC.theWorld.loadedEntityList
                .stream()
                .filter((entity) -> entity instanceof EntityPlayer
                        && isValidEntity((EntityLivingBase) entity))
                .min(Comparator.comparingDouble((entity)
                        -> entity.getDistanceToEntity(MC.thePlayer)))
                .orElse(null);
    }

    private boolean isValidEntity(final EntityLivingBase entity)
    {
        if (!(entity instanceof EntityPlayer)
                || entity.getHealth() <= 0.0f
                || entity.isDead
                || entity.equals(MC.thePlayer)
                || entity.getEntityId() == FreecamCheat.CAMERA_ENTITY_ID)
        {
            return false;
        }
        final double distanceSq = MC.thePlayer.getDistanceSqToEntity(entity);
        if (distanceSq > rangeSetting.getValue() * rangeSetting.getValue())
        {
            return false;
        }
        return !Nebula.INSTANCE.getFriendManager().isFriend((EntityPlayer) entity);
    }

    private float calcDamage(final EntityPlayer entity, final BlockPos pos)
    {
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();

        if (y < 0)
        {
            return 0.0f;
        }

        final double distanceScaled = entity.getDistance(x, y, z) / BED_EXPLOSION_SIZE;
        if (distanceScaled > 1.0)
        {
            return 0.0f;
        }

        final double v = (1.0 - distanceScaled) * entity.worldObj.getBlockDensity(
                Vec3.createVectorHelper(x + 0.5, y + 0.5, z + 0.5),
                entity.boundingBox.copy());

        float damage = getDamageAfterAbsorb(
                getDmgMultiplier((float) ((v * v + v) / 2.0 * 8.0 * BED_EXPLOSION_SIZE + 1.0)),
                entity.getTotalArmorValue(),
                (float) entity.getEntityAttribute(SharedMonsterAttributes.knockbackResistance)
                        .getAttribute().getDefaultValue());

        final Explosion explosion = new Explosion(entity.worldObj,
                entity,
                x + 0.5, y + 0.5, z + 0.5,
                BED_EXPLOSION_STRENGTH);
        explosion.isFlaming = true;
        explosion.isSmoking = true;

        final DamageSource damageSource = DamageSource.setExplosionSource(explosion);
        final int modifier = EnchantmentHelper.getEnchantmentModifierDamage(
                entity.inventory.armorInventory, damageSource);
        damage = getDamageAfterMagicAbsorb(damage, modifier);
        damage = (float) EnchantmentProtection.func_92092_a(MC.thePlayer, damage);

        if (entity.isPotionActive(Potion.resistance.id))
        {
            final int amp = MC.thePlayer.getActivePotionEffect(Potion.resistance).getAmplifier();
            damage = damage * (25.0f - (amp + 1.0f) * 5.0f) / 25.0f;
        }
        return (float) Math.max(0.0, damage);
    }

    private float getDamageAfterAbsorb(final float damage, final float armor, final float toughness)
    {
        float f = 2.0F + toughness / 4.0f;
        float f1 = MathHelper.clamp_float(armor - damage / f, armor * 0.2f, 20.0f);
        return damage * (1.0f - f1 / 25.0f);
    }

    private float getDamageAfterMagicAbsorb(final float damage, final int enchantModifiers)
    {
        return damage * (1.0f - (float) MathHelper.clamp_int(enchantModifiers, 0, 20) / 25.0f);
    }

    private float getDmgMultiplier(final float damage)
    {
        switch (MC.theWorld.difficultySetting)
        {
            case EASY:
            {
                return Math.min(damage / 2.0f + 1.0f, damage);
            }
            case HARD:
            {
                return damage * 3.0f / 2.0f;
            }
            default:
            {
                return damage;
            }
        }
    }

    private boolean isInOverworld()
    {
        return MC.thePlayer.dimension == 0;
    }

    @Override
    public String getMetadata()
    {
        if (target != null)
        {
            return target.getCommandSenderName();
        }
        return super.getMetadata();
    }

    private static final class BedBlockInfo extends BlockInfo
    {
        private float targetDamage = 1.0f, localDamage;

        public BedBlockInfo(final BedBlockInfo info)
        {
            this(info.getPos(), info.getFacing());
            targetDamage = info.getTargetDamage();
            localDamage = info.getLocalDamage();
        }

        public BedBlockInfo(BlockPos pos, EnumFacing facing)
        {
            super(pos, facing);
        }

        public void copy(final BedBlockInfo info)
        {
            setPos(info.getPos());
            setFacing(info.getFacing());
            targetDamage = info.getTargetDamage();
            localDamage = info.getLocalDamage();
        }

        public void invalidate()
        {
            setPos(null);
            setFacing(null);
            targetDamage = 0.0f;
            localDamage = 1.0f;
        }

        public float getTargetDamage()
        {
            return targetDamage;
        }

        public void setTargetDamage(float targetDamage)
        {
            this.targetDamage = targetDamage;
        }

        public float getLocalDamage()
        {
            return localDamage;
        }

        public void setLocalDamage(float localDamage)
        {
            this.localDamage = localDamage;
        }
    }
}
