package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.Timer;

public class ChestStealer extends Module {
    public ChestStealer() {
        super("ChestStealer", ModuleCategory.WORLD);
    }

    private final Value<Integer> delay = new Value<>("Delay", 2, 0, 10);

    private final Timer timer = new Timer();

    @EventListener
    public void onTick(TickEvent event) {
        if (mc.currentScreen instanceof GuiChest && mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;
            IInventory lower = container.getLowerChestInventory();

            if (timer.passedTime(delay.getValue() * 50L, false)) {
                for (int i = 0; i < container.numRows * 9; ++i) {
                    ItemStack stack = lower.getStackInSlot(i);
                    if (stack == null) {
                        continue;
                    }

                    timer.resetTime();
                    mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
                    break;
                }
            }
        }
    }
}
