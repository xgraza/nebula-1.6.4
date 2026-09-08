package ez.nebula.client.impl.module.combat;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.impl.module.exploit.EnderchestBPModule;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import ez.nebula.client.util.minecraft.player.ItemUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xgraza
 * @since 03/20/25
 */
@ModuleManifest(name = "AutoArmor",
        description = "Automatically equips the best armor from your inventory",
        category = ModuleCategory.COMBAT)
public final class AutoArmorModule extends Module
{
    private static final Map<Enchantment, Float> ENCHANTMENT_WEIGHT_MAP = new HashMap<>();
    private static final int ARMOR_SLOT_INDEX_MAX = 8;

    static
    {
        ENCHANTMENT_WEIGHT_MAP.put(Enchantment.protection, 2.0f);
        ENCHANTMENT_WEIGHT_MAP.put(Enchantment.projectileProtection, 1.5f);
        ENCHANTMENT_WEIGHT_MAP.put(Enchantment.fireProtection, 1.5f);
        ENCHANTMENT_WEIGHT_MAP.put(Enchantment.blastProtection, 1.5f);
        ENCHANTMENT_WEIGHT_MAP.put(Enchantment.unbreaking, 1.25f);
        ENCHANTMENT_WEIGHT_MAP.put(Enchantment.aquaAffinity, 1.05f);
        ENCHANTMENT_WEIGHT_MAP.put(Enchantment.thorns, 0.85f);
    }

    private final Setting<Boolean> destackSetting = builder("Destack", true)
            .setDescription("If to only use one armor piece instead of the whole stacked armor stack")
            .build();
    private final Setting<Boolean> noThornsSetting = builder("Prefer No Thorns", false)
            .setDescription("If when possible to avoid equipping armor pieces with the Thorns enchantment")
            .build();
    private final Setting<Boolean> tickSetting = builder("Tick", false)
            .setDescription("If to wait a player tick before equipping the next piece of armor")
            .build();

    private final int[] armorPieces = new int[4];
    private final float[] equippedArmorScores = new float[4];

    @Override
    public void onDisable()
    {
        super.onDisable();
        Arrays.fill(armorPieces, -1);
        Arrays.fill(equippedArmorScores, -1.0f);
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        final boolean replace = cacheBestArmor();
        if (!replace)
        {
            return;
        }

        if (EnderchestBPModule.INSTANCE.isActive())
        {
            notifyWarn("EnderChestBP interfered with AutoArmor, so it was turned off.", 7500L);
            EnderchestBPModule.INSTANCE.closeEnderChestGUI();
            EnderchestBPModule.INSTANCE.setToggled(false);
        }

        if (MC.thePlayer.openContainer.windowId != InventoryUtil.PLAYER_INVENTORY_WINDOW_ID)
        {
            return;
        }

        for (int i = 0; i < armorPieces.length; ++i)
        {
            final int slot = armorPieces[i];
            if (slot == -1)
            {
                continue;
            }

            final int inventorySlot = InventoryUtil.toPacketSlot(slot);
            final int armorSlot = ARMOR_SLOT_INDEX_MAX - i;
            if (MC.thePlayer.inventory.armorInventory[i] != null)
            {
                InventoryUtil.windowClick(armorSlot, InventoryUtil.ClickType.DROP_ALL);
            }

            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(slot);
            if (destackSetting.getValue() && (itemStack != null && itemStack.stackSize > 1))
            {
                InventoryUtil.windowClick(inventorySlot, InventoryUtil.ClickType.PICKUP);
                InventoryUtil.windowClick(armorSlot, InventoryUtil.ClickType.RIGHT_CLICK);
                InventoryUtil.windowClick(inventorySlot, InventoryUtil.ClickType.PICKUP); // put back
            } else
            {
                InventoryUtil.windowClick(inventorySlot, InventoryUtil.ClickType.SHIFT_CLICK);
            }
            armorPieces[i] = -1;
            if (tickSetting.getValue())
            {
                return;
            }
        }
    };

    private boolean cacheBestArmor()
    {
        for (int i = 0; i < 4; ++i)
        {
            equippedArmorScores[i] = -1.0f;
            armorPieces[i] = -1;
        }

        for (int slot = 0; slot < InventoryUtil.PLAYER_INVENTORY_ARMOR_SIZE; ++slot)
        {
            final ItemStack stack = MC.thePlayer.inventory.armorInventory[slot];
            if (stack != null && stack.getItem() instanceof ItemArmor)
            {
                equippedArmorScores[slot] = getArmorScore(stack, ((ItemArmor) stack.getItem()));
            }
        }

        boolean shouldReplaceArmor = false;
        for (int slot = 0; slot < InventoryUtil.PLAYER_INVENTORY_SIZE; ++slot)
        {
            final ItemStack stack = MC.thePlayer.inventory.getStackInSlot(slot);
            if (stack == null || !(stack.getItem() instanceof ItemArmor))
            {
                continue;
            }
            final ItemArmor armor = (ItemArmor) stack.getItem();
            final float armorScore = getArmorScore(stack, armor);
            final int armorIndex = 3 - armor.armorType;
            if (armorScore > equippedArmorScores[armorIndex])
            {
                equippedArmorScores[armorIndex] = armorScore;
                armorPieces[armorIndex] = slot;
                shouldReplaceArmor = true;
            }
        }
        return shouldReplaceArmor;
    }

    private float getArmorScore(final ItemStack stack, final ItemArmor armor)
    {
        float score = armor.damageReduceAmount * 1.2f;
        for (final Enchantment enchantment : ENCHANTMENT_WEIGHT_MAP.keySet())
        {
            final int level = ItemUtil.getEnchantLevel(enchantment, stack);
            if (level <= 0)
            {
                continue;
            }
            float weightedScore = level * ENCHANTMENT_WEIGHT_MAP.get(enchantment);
            if (noThornsSetting.getValue() && enchantment.effectId == Enchantment.thorns.effectId)
            {
                weightedScore -= level;
            }
            score += weightedScore;
        }
        return score;
    }
}
