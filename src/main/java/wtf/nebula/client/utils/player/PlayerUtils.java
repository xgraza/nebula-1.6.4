package wtf.nebula.client.utils.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.utils.client.Wrapper;
import wtf.nebula.client.utils.world.WorldUtils;

public class PlayerUtils implements Wrapper {
    public static Vec3 getPosUnder() {
        return new Vec3(
                Vec3.fakePool,
                Math.floor(mc.thePlayer.posX),
                mc.thePlayer.posY - 2,
                Math.floor(mc.thePlayer.posZ));
    }

    public static Vec3 getPosAt() {
        return new Vec3(
                Vec3.fakePool,
                Math.floor(mc.thePlayer.posX),
                mc.thePlayer.boundingBox.minY,
                Math.floor(mc.thePlayer.posZ));
    }

    public static boolean isOverLiquid() {
        Block block = WorldUtils.getBlock(getPosUnder());
        return block instanceof BlockStaticLiquid || block instanceof BlockLiquid;
    }

    public static EnumFacing getFacing() {
        float[] serverRotations = Nebula.getInstance().getRotationManager().getServerRotation();
        float yaw = serverRotations == null ? mc.thePlayer.rotationYaw : serverRotations[0];
        return getFacing(yaw);
    }

    public static EnumFacing getFacing(float yaw) {
        return EnumFacing.getHorizontal(MathHelper.floor_double((double) (yaw * 4.0F / 360.0F) + 0.5D) & 3);
    }

    public static boolean isHolding(Item item) {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem().equals(item);
    }

    public static boolean isHolding(Class<? extends Item> clazz) {
        return mc.thePlayer.getHeldItem() != null && clazz.isInstance(mc.thePlayer.getHeldItem().getItem());
    }

    public static boolean isUnkillable(EntityPlayer player) {
        PlayerCapabilities capabilities = player.capabilities;
        if (capabilities == null) {
            return false;
        }

        return capabilities.isCreativeMode || capabilities.disableDamage;
    }

    public static void stopUseCurrentItem() {
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(5, 0, 0, 0, 255));
        mc.thePlayer.stopUsingItem();
    }

}
