package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.ItemBow;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet14BlockDig;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class BowRelease extends Module {
    public BowRelease() {
        super("BowRelease", ModuleCategory.COMBAT);
    }

    public final Value<Integer> ticks = new Value<>("Ticks", 3, 3, 20);

    @EventListener
    public void onTick(TickEvent event) {
        ItemStack stack = mc.thePlayer.getHeldItem();
        if (stack != null && stack.getItem() instanceof ItemBow && mc.thePlayer.getItemInUseDuration() >= ticks.getValue()) {
            mc.thePlayer.sendQueue.addToSendQueue(new Packet14BlockDig(5, 0, 0, 0, 255));
            mc.thePlayer.stopUsingItem();
        }
    }
}
