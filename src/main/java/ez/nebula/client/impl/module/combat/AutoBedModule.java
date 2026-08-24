package ez.nebula.client.impl.module.combat;

import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.impl.module.player.AutoEatModule;
import ez.nebula.client.impl.module.render.NameProtectModule;
import ez.nebula.client.util.minecraft.network.PacketUtil;
import ez.nebula.client.util.minecraft.world.DamageUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBed;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.src.BlockPos;
import net.minecraft.util.*;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.player.InteractionManager;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.render.QuadMask;
import ez.nebula.client.impl.module.player.FreecamModule;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.render.EventRender3D;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.render.RenderUtil;
import ez.nebula.client.util.minecraft.world.BlockInfo;
import ez.nebula.client.util.minecraft.world.BlockUtil;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author xgraza
 * @since 06/06/25
 */
@ModuleManifest(name = "AutoBed",
        description = "Automatically places and breaks beds to damage another player",
        category = ModuleCategory.COMBAT)
public final class AutoBedModule extends Module
{
    @ModuleInstance
    public static AutoBedModule INSTANCE;

    private static final int AUTO_BED_ROTATION_PRIORITY = 150;
    private static final double BED_EXPLOSION_SIZE = 5.0;
    private static final float BED_EXPLOSION_STRENGTH = 10.0f;

    private final NumberSetting<Float> rangeSetting = numberBuilder("Range", 4.2f)
            .setMin(1.0f)
            .setMax(6.0f)
            .setScale(0.1f)
            .setDescription("The range should the target be attacked from")
            .build();
    private final NumberSetting<Integer> yRangeSetting = numberBuilder("Y-Range", 1)
            .setMin(1)
            .setMax(5)
            .setScale(1)
            .setDescription("How many y-levels to explore for place positions")
            .build();
    private final Setting<Boolean> extinguishFireSetting = builder("Extinguish Fire", true)
            .setDescription("If to extinguish fire before placing a bed")
            .build();
    private final Setting<Boolean> packetSetting = builder("Observe Packet", false)
            .setDescription("If to observe incoming packets for block changes")
            .build();

    private final NumberSetting<Float> minDamageSetting = numberBuilder("Min Damage", 6.0f)
            .setMin(1.0f)
            .setMax(19.5f)
            .setScale(0.1f)
            .setDescription("The absolute minimum damage a position needs to be considered")
            .build();
    private final Setting<Boolean> averageDamageSetting = builder("Average Damage", true)
            .setDescription("If to average the blast damage between the head and feet block of the bed")
            .build();

    private final Setting<Boolean> suicideSetting = builder("Suicide", false)
            .setDescription("If to ignore local player damage when finding a bed place position")
            .build();
    private final NumberSetting<Float> lethalHealthSetting = numberBuilder("Lethal Health", 12.0f)
            .setMin(2.0f)
            .setMax(19.5f)
            .setScale(0.1f)
            .setDescription("The maximum amount of damage allowed to the local player")
            .setVisibility((value) -> !suicideSetting.getValue())
            .build();
    private final NumberSetting<Float> lethalMultiplierSetting = numberBuilder("Lethal Multiplier", 1.2f)
            .setMin(1.0f)
            .setMax(2.0f)
            .setScale(0.1f)
            .setDescription("The multiplier to the local damage")
            .setVisibility((value) -> !suicideSetting.getValue())
            .build();
    private final NumberSetting<Float> swapPenaltySetting = numberBuilder("Swap Penalty", 2.0f)
            .setMin(0.5f)
            .setMax(12.0f)
            .setScale(0.1f)
            .setDescription("The minimum damage difference between the current position and a new position to be considered")
            .build();

    private final Setting<Boolean> renderSetting = builder("Render", true)
            .setDescription("If to render the bed placement position")
            .build();

    private final BedBlockInfo blockInfo = new BedBlockInfo(null, null);
    private int bedSlot = InventoryUtil.INVALID_SLOT;
    private EntityPlayer target;

    @Override
    public void onDisable()
    {
        super.onDisable();
        AutoGGModule.INSTANCE.setLastTarget(null);
        target = null;
        blockInfo.invalidate();
        bedSlot = InventoryUtil.INVALID_SLOT;
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
        MC.mcProfiler.startSection("autoBed");
        final AxisAlignedBB bb = new AxisAlignedBB(blockInfo.getPos())
                .addCoord(blockInfo.getFacing().getFrontOffsetX(),
                        blockInfo.getFacing().getFrontOffsetY(),
                        blockInfo.getFacing().getFrontOffsetZ());
        bb.maxY = blockInfo.getPos().getY() + 0.5;
        RenderUtil.renderOutlinedAABB(bb, 1.5f, QuadMask.ALL_FACES, 0xFFFF0000);
        RenderUtil.renderFilledAABB(bb, QuadMask.ALL_FACES, 0x80FF0000);
        final Vec3 center = bb.getCenter();
        RenderUtil.renderGLBillboard(center.xCoord, center.yCoord, center.zCoord, 0.2, () ->
        {
            final String text = String.format("%.2f", blockInfo.getTargetDamage());
            MC.fontRenderer.drawStringWithShadow(text, -MC.fontRenderer.getStringWidth(text) / 2, 2, -1);
        });
        MC.mcProfiler.endSection();
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
            AutoGGModule.INSTANCE.setLastTarget(null);
            target = getPlayerInRange();
            return;
        }
        AutoGGModule.INSTANCE.setLastTarget(target);
        bedSlot = InventoryUtil.getHotbarItem(ItemBed.class);
        if (bedSlot == InventoryUtil.INVALID_SLOT)
        {
            return;
        }
        calculatePlacePosition();
        if (blockInfo.getPos() == null || blockInfo.getFacing() == null)
        {
            return;
        }

        if (AutoEatModule.INSTANCE.isActive() || AutoPotModule.INSTANCE.isActive())
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
                InteractionManager.INSTANCE.swingItem();
            }

            if (BlockUtil.isFire(pos2))
            {
                MC.playerController.clickBlock(pos2.getX(), pos2.getY(), pos2.getZ(), EnumFacing.UP.order_a);
                InteractionManager.INSTANCE.swingItem();
            }
        }

        tryPlaceBreakBed();
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S23PacketBlockChange && packetSetting.getValue() && blockInfo.getPos() != null)
        {
            final S23PacketBlockChange packet = event.getPacket();
            final BlockPos placePos = blockInfo.getPos();
            final BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
            if (!placePos.equals(pos))
            {
                return;
            }

            final float damage = calcDamage(MC.thePlayer, pos) * lethalMultiplierSetting.getValue();
            if (!suicideSetting.getValue() && damage > lethalHealthSetting.getValue())
            {
                return;
            }

            if (packet.getType() instanceof BlockBed)
            {
                PacketUtil.send(new C08PacketPlayerBlockPlacement(
                        packet.getX(), packet.getY(), packet.getZ(),
                        EnumFacing.UP.order_a,
                        null,
                        0.5f, 0.5f, 0.5f));
            } else if (packet.getType() instanceof BlockAir)
            {
                MC.theWorld.setBlockToAir(packet.getX(), packet.getY(), packet.getZ());
                tryPlaceBreakBed();
            }
        }
    };

    private void tryPlaceBreakBed()
    {
        if (bedSlot == InventoryUtil.INVALID_SLOT || blockInfo.getPos() == null || blockInfo.getFacing() == null)
        {
            return;
        }
        Nebula.INSTANCE.getInventoryManager().setSlot(bedSlot);
        if (Nebula.INSTANCE.getRotationManager().spoof(
                BlockUtil.getHorizontalFacing(blockInfo.getFacing()) * 90.0f,
                0.0f, AUTO_BED_ROTATION_PRIORITY))
        {
            if (InteractionManager.INSTANCE.rightClickBlock(blockInfo.getPos().down(), EnumFacing.UP))
            {
                InteractionManager.INSTANCE.rightClickBlock(blockInfo.getPos(), EnumFacing.UP);
            }
        }
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    }

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
                final float lethalMulti = lethalMultiplierSetting.getValue();
                final float localDmg1 = calcDamage(MC.thePlayer, bedOrigin) * lethalMulti;
                final float localDmg2 = calcDamage(MC.thePlayer, bedNeighbor) * lethalMulti;
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
                if (localDamage >= lethal)
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
                || entity.getEntityId() == FreecamModule.CAMERA_ENTITY_ID)
        {
            return false;
        }
        final double distanceSq = MC.thePlayer.getDistanceSqToEntity(entity);
        if (distanceSq > rangeSetting.getValue() * rangeSetting.getValue())
        {
            return false;
        }
        return NoFriendsModule.INSTANCE.isToggled() || !Nebula.INSTANCE.getFriendManager().isFriend((EntityPlayer) entity);
    }

    private float calcDamage(final EntityPlayer entity, final BlockPos pos)
    {
        return DamageUtil.getExplosionDamage(entity, pos, BED_EXPLOSION_SIZE, BED_EXPLOSION_STRENGTH);
    }

    private boolean isInOverworld()
    {
        return MC.thePlayer.dimension == 0;
    }

    public EntityPlayer getTarget()
    {
        return target;
    }

    @Override
    public boolean isActive()
    {
        return super.isActive() && target != null && blockInfo.getPos() != null && blockInfo.getFacing() != null;
    }

    @Override
    public String getMetadata()
    {
        if (target != null)
        {
            return NameProtectModule.INSTANCE.protect(target.getCommandSenderName());
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
