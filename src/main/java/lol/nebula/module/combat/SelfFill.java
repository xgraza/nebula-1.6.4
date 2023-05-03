package lol.nebula.module.combat;

import com.google.common.collect.Lists;
import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.EventStage;
import lol.nebula.listener.events.entity.EventPushOutOfBlocks;
import lol.nebula.listener.events.entity.EventWalkingUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.Pair;
import lol.nebula.util.math.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.List;

import static lol.nebula.util.world.WorldUtils.isReplaceable;

/**
 * @author aesthetical
 * @since 05/02/23
 */
public class SelfFill extends Module {

    private static final List<Block> WHITELIST = Lists.newArrayList(Blocks.obsidian, Blocks.ender_chest, Blocks.anvil);

    private final Setting<Boolean> rotate = new Setting<>(true, "Rotate");
    private final Setting<Boolean> keep = new Setting<>(true, "Keep");

    public SelfFill() {
        super("Self Fill", "Prevents explosions from hurting your weakest point", ModuleCategory.COMBAT);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (mc.thePlayer != null) Nebula.getInstance().getInventory().sync();
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {

        Vec3 vec = mc.thePlayer.getGroundPosition();
        if (isReplaceable(vec)) {

            Pair<Vec3, EnumFacing> next = new Pair<>(vec.addVector(0, -1, 0), EnumFacing.UP);
            if (event.getStage() == EventStage.POST) {
                place(next);
            }
        }


    }

    @Listener
    public void onPushOutOfBlocks(EventPushOutOfBlocks event) {
        if (mc.thePlayer != null && mc.thePlayer.equals(event.getEntity())) event.cancel();
    }

    private void place(Pair<Vec3, EnumFacing> placement) {
        int slot = getBlockSlot();
        if (slot == -1) return;

        Nebula.getInstance().getInventory().setSlot(slot);

        if (rotate.getValue()) {
            float[] angles = RotationUtils.toBlock(placement.getKey(), placement.getValue());
            Nebula.getInstance().getRotations().spoof(angles);
            mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C06PacketPlayerPosLook(
                    mc.thePlayer.posX, mc.thePlayer.boundingBox.minY, mc.thePlayer.posY, mc.thePlayer.posZ,
                    angles[0], angles[1], mc.thePlayer.onGround));
        }

        Vec3 hitVec = placement.getKey().addVector(0.5, 0.5, 0.5);

        float facingX = (float) (hitVec.xCoord - placement.getKey().xCoord);
        float facingY = (float) (hitVec.yCoord - placement.getKey().yCoord);
        float facingZ = (float) (hitVec.zCoord - placement.getKey().zCoord);

        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                (int) placement.getKey().xCoord,
                (int) placement.getKey().yCoord,
                (int) placement.getKey().zCoord,
                placement.getValue().getOrder_a(),
                mc.thePlayer.inventory.getStackInSlot(slot),
                facingX,
                facingY,
                facingZ
        ));
        mc.thePlayer.swingItemSilent();

        Nebula.getInstance().getInventory().sync();

        if (!keep.getValue()) setState(false);
    }

    private int getBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null
                    && stack.getItem() instanceof ItemBlock
                    && WHITELIST.contains(((ItemBlock) stack.getItem()).field_150939_a)) return i;
        }
        return -1;
    }
}
