package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.player.PlayerUtils;

public class FastUse extends ToggleableModule {
    private final Property<Boolean> blocks = new Property<>(true, "Blocks");
    private final Property<Boolean> exp = new Property<>(true, "EXP", "experience");

    private final Property<Boolean> bows = new Property<>(false, "Bows", "bowandarrow");
    private final Property<Integer> bowTicks = new Property<>(3, 3, 20, "Bow Ticks")
            .setVisibility(bows::getValue);

    public FastUse() {
        super("Fast Use", new String[]{"fastuse", "fastplace"}, ModuleCategory.WORLD);
        offerProperties(blocks, exp, bows, bowTicks);
    }

    @EventListener
    public void onTick(EventTick event) {
        if (blocks.getValue() && PlayerUtils.isHolding(ItemBlock.class) || exp.getValue() && PlayerUtils.isHolding(Items.experience_bottle)) {
            mc.rightClickDelayTimer = 0;
        }

        if (bows.getValue() && PlayerUtils.isHolding(ItemBow.class)) {
            int useCount = mc.thePlayer.getItemInUseDuration();
            if (useCount >= bowTicks.getValue()) {
                PlayerUtils.stopUseCurrentItem();
            }
        }
    }
}
