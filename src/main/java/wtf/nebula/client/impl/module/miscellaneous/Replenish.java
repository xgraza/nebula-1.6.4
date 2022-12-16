package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.player.PlayerUtils;

public class Replenish extends ToggleableModule {
    public final Property<Integer> percent = new Property<>(30, 30, 95, "Percent");
    public final Property<Integer> delay = new Property<>(6, 0, 10, "Delay", "dl");
    public final Property<Boolean> noIllegals = new Property<>(true, "No Illegals", "noillegals", "antiillegal");
    private final Property<Boolean> strict = new Property<>(true, "Strict");

    private final Timer timer = new Timer();

    public Replenish() {
        super("Replenish", new String[]{"hotbarrefill", "refill", "refiller"}, ModuleCategory.MISCELLANEOUS);
        offerProperties(percent, delay, noIllegals, strict);
    }

    @EventListener
    public void onTick(EventTick event) {
        if (timer.hasPassed(delay.getValue().longValue() * 50L, false)) {

            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null || stack.getItem() == null || stack.getMaxStackSize() == 1) {
                    continue;
                }

                if (noIllegals.getValue() && (stack.stackSize < 0 || Math.abs(stack.stackSize) > stack.getMaxStackSize())) {
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

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getEra().equals(Era.PRE) && event.getPacket() instanceof C0EPacketClickWindow && strict.getValue()) {
            if (mc.thePlayer.isUsingItem()) {
                PlayerUtils.stopUseCurrentItem();
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

                Block hotbarBlock = ((ItemBlock) stack.getItem()).block;
                Block invBlock = ((ItemBlock) itemStack.getItem()).block;

                if (hotbarBlock.equals(invBlock)) {
                    s = i;
                }
            } else {
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
