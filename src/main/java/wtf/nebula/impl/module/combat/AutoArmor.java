package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.EnumArmorMaterial;
import net.minecraft.src.ItemArmor;
import net.minecraft.src.ItemStack;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.Timer;

import java.util.Arrays;

public class AutoArmor extends Module {
    public AutoArmor() {
        super("AutoArmor", ModuleCategory.COMBAT);
    }

    public final Value<Integer> delay = new Value<>("Delay", 4, 0, 20);

    private final Timer timer = new Timer();
    private final int[] bestArmor = { -1, -1, -1, -1 };

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        Arrays.fill(bestArmor, -1);
    }

    @EventListener
    public void onTick(TickEvent event) {

        for (int i = 0; i < 36; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null || !(stack.getItem() instanceof ItemArmor)) {
                continue;
            }

            ItemArmor itemArmor = (ItemArmor) stack.getItem();

            int j = 3 - itemArmor.armorType;
            int reduction = -1;

            ItemStack worn = mc.thePlayer.inventory.armorInventory[j];
            if (worn != null && worn.getItem() instanceof ItemArmor) {
                ItemArmor wornArmor = (ItemArmor) worn.getItem();

                reduction = wornArmor.damageReduceAmount;
                if (wornArmor.getArmorMaterial().equals(EnumArmorMaterial.GOLD)) {
                    reduction -= 4;
                }
            }

            if (itemArmor.armorType > reduction) {
                bestArmor[j] = i;
            }
        }

        if (mc.currentScreen != null) {
            return;
        }

        if (timer.passedTime(50L * delay.getValue().longValue(), false)) {

            for (int i = 0; i < 4; ++i) {
                int armorSlot = bestArmor[i];
                if (armorSlot == -1) {
                    continue;
                }

                int windowId = mc.thePlayer.openContainer.windowId;
                boolean hadItem = mc.thePlayer.inventory.armorInventory[i] != null;

                mc.playerController.windowClick(windowId, armorSlot < 9 ? armorSlot + 36 : armorSlot, 0, 0, mc.thePlayer);
                mc.playerController.windowClick(windowId, 8 - i, 0, 0, mc.thePlayer);

                if (hadItem) {
                    mc.playerController.windowClick(windowId, armorSlot < 9 ? armorSlot + 36 : armorSlot, 0, 0, mc.thePlayer);
                }

                timer.resetTime();
                bestArmor[i] = -1;
                break;
            }
        }
    }
}
