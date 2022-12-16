package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import wtf.nebula.client.impl.event.impl.world.EventClickBlock;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.world.WorldUtils;

public class AutoTool extends ToggleableModule {
    public AutoTool() {
        super("Auto Tool", new String[]{"autotool", "toolswap", "toolswitch"}, ModuleCategory.WORLD);
    }

    @EventListener
    public void onClickBlock(EventClickBlock event) {
        int slot = getBestToolAt(event.getX(), event.getY(), event.getZ());
        if (slot == -1) {
            return;
        }

        mc.thePlayer.inventory.currentItem = slot;
    }

    public static int getBestToolAt(int x, int y, int z) {
        Block block = WorldUtils.getBlock(x, y, z);
        if (block.getBlockHardness(mc.theWorld, x, y, z) == -1) {
            return -1;
        }

        int slot = -1;
        float dmg = 0.0f;

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null || stack.getItem() == null) {
                continue;
            }

            float toolDmg = stack.getItem().func_150893_a(stack, block);
            if (toolDmg > dmg) {
                slot = i;
                dmg = toolDmg;
            }
        }

        return slot;
    }
}
