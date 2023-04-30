package lol.nebula.module.visual;

import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.item.ItemStack;

/**
 * @author aesthetical
 * @since 04/30/23
 */
public class InfiniteViewer extends Module {

    private final Setting<Boolean> simple = new Setting<>(false, "Simple");

    public InfiniteViewer() {
        super("Infinite Viewer", "Allows you to see how much of an item there truly is", ModuleCategory.VISUAL);
    }

    /**
     * Formats an item stack count
     * @param stack the stack count
     * @return the formatted stack count text
     */
    public String format(ItemStack stack) {
        if (stack.stackSize > 1) return String.valueOf(stack.stackSize);
        return simple.getValue() ? "-" : String.valueOf(stack.stackSize);
    }
}
