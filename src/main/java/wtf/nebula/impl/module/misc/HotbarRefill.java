package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Block;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.Timer;

public class HotbarRefill extends Module {
    public HotbarRefill() {
        super("HotbarRefill", ModuleCategory.MISC);
    }

    public final Value<Integer> percent = new Value<>("Percent", 30, 30, 75);
    public final Value<Integer> delay = new Value<>("Delay", 6, 0, 10);
    public final Value<Boolean> noIllegals = new Value<>("NoIllegals", true);

    private final Timer timer = new Timer();

    @EventListener
    public void onTick(TickEvent event) {
        if (timer.passedTime(delay.getValue().longValue() * 50L, false)) {

            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null || stack.getItem() == null || stack.getMaxStackSize() == 1) {
                    continue;
                }

                if (noIllegals.getValue() && Math.abs(stack.stackSize) > stack.getMaxStackSize()) {
                    continue;
                }

                double percentage = ((double) stack.stackSize / (double) stack.getMaxStackSize()) * 100.0;
                if (percentage > percent.getValue().doubleValue()) {
                    continue;
                }

                boolean result = refill(stack, i);
                if (result) {
                    timer.resetTime();
                    return;
                }
            }
        }
    }

    private boolean refill(ItemStack stack, int slotId) {
        int s = -1;

        for (int i = 9; i < 36; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null || itemStack.getItem() == null) {
                continue;
            }

            if (itemStack.hasDisplayName() && !itemStack.getDisplayName().equals(stack.getDisplayName())) {
                continue;
            }

            if (stack.getItem() instanceof ItemBlock) {

                if (!(itemStack.getItem() instanceof ItemBlock)) {
                    continue;
                }

                int hotbarBlock = ((ItemBlock) stack.getItem()).getBlockID();
                int invBlock = ((ItemBlock) itemStack.getItem()).getBlockID();

                if (hotbarBlock == invBlock) {
                    s = i;
                }
            }

            else {
                if (stack.getItem().equals(itemStack.getItem())) {
                    s = i;
                }
            }
        }

        if (s == -1) {
            return false;
        }

        int windowId = mc.thePlayer.openContainer.windowId;

        mc.playerController.windowClick(windowId, s, 0, 0, mc.thePlayer);
        mc.playerController.windowClick(windowId, slotId + 36, 0, 0, mc.thePlayer);
        mc.playerController.windowClick(windowId, s, 0, 0, mc.thePlayer);

        return true;
    }
}
