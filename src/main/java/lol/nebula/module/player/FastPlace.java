package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class FastPlace extends Module {
    private final Setting<Integer> speed = new Setting<>(4, 1, 4, "Speed");
    private final Setting<Boolean> blocks = new Setting<>(true, "Blocks");
    private final Setting<Boolean> exp = new Setting<>(true, "Experience");

    public FastPlace() {
        super("Fast Place", "Removes the place delay in the vanilla game", ModuleCategory.PLAYER);
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        ItemStack s = mc.thePlayer.getHeldItem();

        // if the stack is null, do not continue
        if (s == null) return;

        // exempts
        if (!blocks.getValue() && s.getItem() instanceof ItemBlock
                || !exp.getValue() && s.getItem() == Items.experience_bottle) return;

        // 0 = no delay
        mc.rightClickDelayTimer = 4 - speed.getValue();
    }
}
