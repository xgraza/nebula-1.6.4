package lol.nebula.module.combat;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.timing.Timer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class AutoArmor extends Module {

    private final Setting<Double> delay = new Setting<>(0.5, 0.01, 0.0, 10.0, "Delay");

    private final Timer timer = new Timer();
    private final int[] bestArmor = new int[4];

    public AutoArmor() {
        super("Auto Armor", "Automatically equips armor", ModuleCategory.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Arrays.fill(bestArmor, -1);
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null || !(stack.getItem() instanceof ItemArmor)) {
                continue;
            }

            float damageReduceAmount = getArmorValue(stack);
            float armorReduction = 0.0f;

            int armorIndex = 3 - ((ItemArmor) stack.getItem()).armorType;

            ItemStack curr = mc.thePlayer.inventory.armorInventory[armorIndex];
            if (curr != null && curr.getItem() instanceof ItemArmor) {
                armorReduction = getArmorValue(curr);
            }

            if (damageReduceAmount > armorReduction) {
                bestArmor[armorIndex] = i;
            }
        }

        if (timer.ms((long) (delay.getValue() * 1000.0), false)) {
            int windowId = mc.thePlayer.openContainer.windowId;
            for (int i = 0; i < bestArmor.length; ++i) {
                int armorSlot = bestArmor[i];
                if (armorSlot != -1) {

                    // if there is no armor in the armor slot, throw it out
                    if (mc.thePlayer.inventory.armorInventory[i] != null) {
                        mc.playerController.windowClick(windowId, 8 - i, 1, 4, mc.thePlayer);
                    }

                    // put the armor piece there
                    mc.playerController.windowClick(windowId, armorSlot < 9 ? armorSlot + 36 : armorSlot, 0, 1, mc.thePlayer);

                    // reset
                    bestArmor[i] = -1;
                    timer.resetTime();

                    return;
                }
            }
        }
    }

    /**
     * Gets the total armor value of an item stack
     * @param stack the item stack
     * @return the armor value of something
     */
    private float getArmorValue(ItemStack stack) {
        float score = ((ItemArmor) stack.getItem()).damageReduceAmount;

        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 1.6f;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) * 1.1f;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack) * 1.1f;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) * 1.1f;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) * 0.5f;
        score += EnchantmentHelper.getEnchantmentLevel(Enchantment.aquaAffinity.effectId, stack) * 0.5f;

        return Math.max(score, 0.0f);
    }
}
