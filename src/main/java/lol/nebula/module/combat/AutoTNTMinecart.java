package lol.nebula.module.combat;

import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.move.EventWalkingUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.feature.DevelopmentFeature;
import lol.nebula.util.math.RotationUtils;
import lol.nebula.util.math.timing.Timer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.List;

import static lol.nebula.util.world.WorldUtils.getHitVec;

/**
 * @author aesthetical
 * @since 05/09/23
 */
@DevelopmentFeature
public class AutoTNTMinecart extends Module {

    private final Setting<Double> delay = new Setting<>(1.0, 0.01, 0.0, 10.0, "Delay");
    private final Setting<Integer> count = new Setting<>(20, 1, 200, "Count");
    private final Setting<Boolean> rotate = new Setting<>(true, "Rotate");

    private final Timer timer = new Timer();
    private Vec3 railPos;
    private int placed;

    public AutoTNTMinecart() {
        super("Auto TNT Minecart", "Automatically adds TNT minecarts to the nearest rail and punches it", ModuleCategory.COMBAT);
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {
        Vec3 next = calcRailPos();
        if (next == null) {
            railPos = null;
            placed = 0;
            return;
        }

        if (!next.equals(railPos)) {
            railPos = next;
            placed = 0;
        }

        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == Items.tnt_minecart) {
                slot = i;
                break;
            }
        }

        if (slot == -1 || placed > count.getValue()) {
            railPos = null;
            return;
        }

        if (rotate.getValue()) RotationUtils.setRotations(event,
                RotationUtils.toBlock(railPos, EnumFacing.UP));

        if (!timer.ms((long) (delay.getValue() * 1000.0), false)) return;

        Nebula.getInstance().getInventory().setSlot(slot);

        boolean result = mc.playerController.onPlayerRightClick(mc.thePlayer,
                mc.theWorld,
                mc.thePlayer.inventory.getStackInSlot(slot),
                (int) railPos.xCoord,
                (int) railPos.yCoord,
                (int) railPos.zCoord,
                EnumFacing.UP.getOrder_a(),
                getHitVec(railPos, EnumFacing.UP));
        if (!result) return;

        timer.resetTime();
        mc.thePlayer.swingItem();

        Nebula.getInstance().getInventory().sync();

    }

    private Vec3 calcRailPos() {
        int r = (int) mc.playerController.getBlockReachDistance();

        for (int x = -r; x <= r; ++x) {
            for (int z = -r; z <= r; ++z) {
                Vec3 pos = new Vec3(Vec3.fakePool,
                        mc.thePlayer.posX + x,
                        mc.thePlayer.boundingBox.minY,
                        mc.thePlayer.posZ + z);

                if (mc.thePlayer.getDistanceSq(
                        pos.xCoord + 0.5,
                        pos.yCoord + 1.0,
                        pos.zCoord + 0.5) > r) continue;

                Block block = mc.theWorld.getBlock((int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord);
                if (block == Blocks.rail) return pos;
            }
        }

        return null;
    }
}
