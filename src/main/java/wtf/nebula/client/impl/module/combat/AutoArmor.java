package wtf.nebula.client.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.ItemArmor;
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

import java.util.Arrays;

public class AutoArmor extends ToggleableModule {
    private final Property<Integer> delay = new Property<>(2, 0, 10, "Delay", "d");
    private final Property<Boolean> stacked = new Property<>(true, "Handle Stacked", "stackhandle", "stacked");
    private final Property<Boolean> strict = new Property<>(true, "Strict", "s");

    private final Timer timer = new Timer();
    private final int[] bestArmor = { -1, -1, -1, -1 };

    public AutoArmor() {
        super("Auto Armor", new String[]{"autoarmor", "armorequip"}, ModuleCategory.COMBAT);
        offerProperties(delay, stacked, strict);
    }

    @Override
    public String getTag() {
        return String.valueOf(delay.getValue());
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        Arrays.fill(bestArmor, -1);
    }

    @EventListener
    public void onTick(EventTick event) {
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
                if (wornArmor.getArmorMaterial().equals(ItemArmor.ArmorMaterial.GOLD)) {
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

        if (timer.hasPassed(50L * delay.getValue().longValue(), false)) {

            for (int i = 0; i < 4; ++i) {
                int armorSlot = bestArmor[i];
                if (armorSlot == -1) {
                    continue;
                }

                int windowId = mc.thePlayer.openContainer.windowId;
                boolean hadItem = mc.thePlayer.inventory.armorInventory[i] != null;

                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack.stackSize > 1 && stacked.getValue()) {

                    // remove the current item from the armor slot
                    if (hadItem) {
                        mc.playerController.windowClick(windowId, 8 - i, 0, 1, mc.thePlayer);
                    }

                    // shift click the stacked armor piece into the armor slots
                    mc.playerController.windowClick(windowId, armorSlot < 9 ? armorSlot + 36 : armorSlot, 0, 1, mc.thePlayer);
                } else {
                    mc.playerController.windowClick(windowId, armorSlot < 9 ? armorSlot + 36 : armorSlot, 0, 0, mc.thePlayer);
                    mc.playerController.windowClick(windowId, 8 - i, 0, 0, mc.thePlayer);

                    if (hadItem) {
                        mc.playerController.windowClick(windowId, armorSlot < 9 ? armorSlot + 36 : armorSlot, 0, 0, mc.thePlayer);
                    }
                }

                timer.resetTime();
                bestArmor[i] = -1;

                break;
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
}
