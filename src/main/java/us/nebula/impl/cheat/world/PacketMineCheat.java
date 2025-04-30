package us.nebula.impl.cheat.world;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import us.nebula.Nebula;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.player.EventAttackBlock;
import us.nebula.impl.event.render.EventRender3D;
import us.nebula.util.player.InventoryUtil;
import us.nebula.util.player.ItemUtil;
import us.nebula.util.render.RenderUtil;
import us.nebula.util.world.BlockUtil;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author xgraza
 * @since 04/02/25
 */
@CheatManifest(name = "PacketMine",
        description = "Mines blocks with packets",
        category = CheatCategory.WORLD)
public final class PacketMineCheat extends Cheat
{
    @CheatInstance
    public static PacketMineCheat INSTANCE;

    private final Setting<Double> percentSetting = new Setting<>(
            "Percent", 0.95, 0.01, 1.0, 0.01);
    private final Setting<Boolean> renderSetting = new Setting<>(
            "Render", true);

    private final Queue<MinePosition> minePositionQueue = new ConcurrentLinkedQueue<>();
    private MinePosition currentPosition;

    @Override
    protected void onDisable()
    {
        super.onDisable();
        if (currentPosition != null && currentPosition.sentBreak && MC.thePlayer != null)
        {
            MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                    0,
                    currentPosition.x, currentPosition.y, currentPosition.z,
                    currentPosition.side));
        }
        if (MC.thePlayer != null)
        {
            Nebula.INSTANCE.getInventoryManager().syncSlot();
        }
        minePositionQueue.clear();
        currentPosition = null;
    }

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        if (currentPosition == null || !renderSetting.getValue())
        {
            return;
        }

        int x = currentPosition.x;
        int y = currentPosition.y;
        int z = currentPosition.z;

        AxisAlignedBB bb = MC.theWorld.getBlock(x, y, z).getSelectedBoundingBoxFromPool(MC.theWorld, x, y, z);
        if (bb == null)
        {
            bb = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
        }

        bb = new AxisAlignedBB(bb.getCenter(), 0.0).copy();

        double factor = MathHelper.clamp_double(currentPosition.progress, 0.0, 1.0);

        bb = bb.expand(factor * 0.5, factor * 0.5, factor * 0.5);

        int color = factor >= percentSetting.getValue() ? 0x800000FF : 0x80FF0000;

        RenderUtil.filledBox3D(bb, 0, color);
        RenderUtil.outlinedBox3D(bb, 1.5f, color);
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        minePositionQueue.removeIf(
                (position) -> MC.thePlayer.getDistanceSq(
                        position.x, position.y, position.z) > 4.5f * 4.5f);
        if (currentPosition == null)
        {
            if (!minePositionQueue.isEmpty())
            {
                currentPosition = minePositionQueue.poll();
            }
            return;
        }
        if (BlockUtil.isReplaceable(currentPosition.x, currentPosition.y, currentPosition.z)
                || MC.thePlayer.getDistanceSq(
                        currentPosition.x, currentPosition.y, currentPosition.z) > 4.5f * 4.5f)
        {
            abortBreakingBlock(currentPosition);
            Nebula.INSTANCE.getInventoryManager().syncSlot();
            currentPosition = null;
            return;
        }

        // begin to break, then wait a tick before calculating progress
        if (!currentPosition.sentBreak)
        {
            currentPosition.progress = 0.0f;
            currentPosition.sentBreak = true;
            MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                    0,
                    currentPosition.x, currentPosition.y, currentPosition.z,
                    currentPosition.side));
            Nebula.INSTANCE.getInventoryManager().syncSlot();
            return;
        }
        currentPosition.progress += getStrength(currentPosition);
        if (currentPosition.progress >= percentSetting.getValue() && !currentPosition.sentStop)
        {
            Nebula.INSTANCE.getInventoryManager().setSlot(currentPosition.slot);
            currentPosition.sentStop = true;
            MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                    2,
                    currentPosition.x, currentPosition.y, currentPosition.z,
                    currentPosition.side));
        }
    };

    @Subscribe
    private final EventListener<EventAttackBlock> attackBlockEventListener = event ->
    {
        if (MC.playerController.isInCreativeMode())
        {
            return;
        }
        final Block block = MC.theWorld.getBlock(event.getX(), event.getY(), event.getZ());
        if (block.blockHardness == -1.0f)
        {
            return;
        }
        int slot = InventoryUtil.getBestToolSlotFor(block);
        if (slot == -1)
        {
            slot = MC.thePlayer.inventory.currentItem;
        }
        event.cancel();
        minePositionQueue.add(new MinePosition(
                event.getX(), event.getY(), event.getZ(),
                event.getSide(),
                slot));
    };

    private void abortBreakingBlock(final MinePosition minePosition)
    {
        MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                1, minePosition.x, minePosition.y, minePosition.z, -1));
    }

    private double getStrength(final MinePosition position)
    {
        final Block block = MC.theWorld.getBlock(position.x, position.y, position.z);
        final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(position.slot);

        if (block.blockHardness < 0.0f)
        {
            return 0.0f;
        }

        final double speed = getDestroySpeed(block, itemStack);
        final double factor = (itemStack != null && itemStack.isProperItemForBlock(block))
                ? 30.0f
                : 100.0f;
        return speed / block.blockHardness / factor;
    }

    private double getDestroySpeed(final Block blockAt, final ItemStack itemStack)
    {
        float breakSpeed = 1.0f;
        if (itemStack != null)
        {
            breakSpeed *= itemStack.getStrVsBlock(blockAt);
        }

        if (breakSpeed > 1.0f)
        {
            final int efficiency = ItemUtil.getEnchantLevelNoLimit(Enchantment.efficiency, itemStack);
            if (efficiency > 0)
            {
                float mod = efficiency * efficiency + 1.0f;
                breakSpeed += (itemStack.isProperItemForBlock(blockAt) ? mod : mod * 0.08f);
            }
        }

        if (MC.thePlayer.isPotionActive(Potion.digSpeed))
        {
            final int hasteMod = MC.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier();
            if (hasteMod > 0)
            {
                breakSpeed *= 1.0f + (hasteMod + 1.0f) * 0.2f;
            }
        }

        if (MC.thePlayer.isPotionActive(Potion.digSlowdown))
        {
            final int fatigueMod = MC.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier();
            if (fatigueMod > 0)
            {
                breakSpeed *= 1.0f - (fatigueMod + 1.0f) * 0.2f;
            }
        }

        if (MC.thePlayer.isInsideOfMaterial(Material.water)
                && !EnchantmentHelper.getAquaAffinityModifier(MC.thePlayer))
        {
            breakSpeed /= 5.0f;
        }

        if (!MC.thePlayer.onGround)
        {
            breakSpeed /= 5.0f; // TODO: ground spoof option?
        }

        return breakSpeed;
    }

    private static final class MinePosition
    {
        public final int x, y, z, side, slot;
        public double progress;
        public boolean sentBreak, sentStop;

        public MinePosition(int x, int y, int z, int side, int slot)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.side = side;
            this.slot = slot;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof MinePosition))
            {
                return false;
            }
            return obj.hashCode() == hashCode();
        }

        @Override
        public int hashCode()
        {
            return (x * 31) + (y * 31) + (z * 31) + (side * 6);
        }
    }
}
