package ez.nebula.client.impl.module.combat;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.impl.module.exploit.EnderchestBPModule;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.minecraft.player.ItemUtil;

import java.util.Arrays;

/**
 * @author xgraza
 * @since 03/20/25
 */
@ModuleManifest(name = "AutoArmor",
        description = "Automatically equips the best armor from your inventory",
        category = ModuleCategory.COMBAT)
public final class AutoArmorModule extends Module
{
    private static final int INVENTORY_WINDOW_ID = 0;
    private static final Enchantment[] RELEVANT_ENCHANTMENTS = {
            Enchantment.thorns,
            Enchantment.protection,
            Enchantment.projectileProtection,
            Enchantment.blastProtection,
            Enchantment.fireProtection,
            Enchantment.aquaAffinity,
            Enchantment.unbreaking
    };

    private final Setting<Boolean> destackSetting = builder("Destack", true)
            .setDescription("If to only use one armor piece instead of the whole stacked armor stack")
            .build();
    private final Setting<Boolean> noThornsSetting = builder("Prefer No Thorns", false)
            .setDescription("If when possible to avoid equipping armor pieces with the Thorns enchantment")
            .build();
    private final Setting<Boolean> tickSetting = builder("Tick", false)
            .setDescription("If to wait a player tick before equipping the next piece of armor")
            .build();
    private final Setting<Boolean> guiCheckSetting = builder("Gui Check", true)
            .setDescription("If to make sure you are not in another container GUI when equipping armor")
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
            notifyInfo("EnderCheatBP interfered with AutoArmor, so it was turned off.", 7500L);
            EnderchestBPModule.INSTANCE.closeEnderChestGUI();
            EnderchestBPModule.INSTANCE.setToggled(false);
        }

        if (MC.thePlayer.openContainer.windowId != INVENTORY_WINDOW_ID && guiCheckSetting.getValue())
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

            final int packetSlot = slot < 9 ? slot + 36 : slot;
            if (MC.thePlayer.inventory.armorInventory[i] != null)
            {
                MC.playerController.windowClick(INVENTORY_WINDOW_ID, 8 - i, 1, 4, MC.thePlayer);
            }

            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(slot);
            if (destackSetting.getValue() && (itemStack != null && itemStack.stackSize > 1))
            {
                // picks up armor
                MC.playerController.windowClick(INVENTORY_WINDOW_ID, packetSlot, 0, 0, MC.thePlayer);

                // right clicks the armor into the empty armor slot (to take one off the stack of armor)
                MC.playerController.windowClick(INVENTORY_WINDOW_ID, 8 - i, 1, 0, MC.thePlayer);

                // places the stacked armor back in its original slot
                MC.playerController.windowClick(INVENTORY_WINDOW_ID, packetSlot, 0, 0, MC.thePlayer);
            } else
            {
                // shift click into slot
                MC.playerController.windowClick(INVENTORY_WINDOW_ID, packetSlot, 0, 1, MC.thePlayer);
            }

            armorPieces[i] = -1;
            // run next click next tick
            if (tickSetting.getValue())
            {
                return;
            }
        }
    };

    private boolean cacheBestArmor()
    {
        Arrays.fill(equippedArmorScores, -1.0f);
        for (int i = 0; i < 4; ++i)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.armorInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof ItemArmor)
            {
                equippedArmorScores[i] = getArmorScore(
                        itemStack, ((ItemArmor) itemStack.getItem()));
            }
        }

        boolean shouldReplaceArmor = false;
        for (int i = 0; i < 36; ++i)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null || !(itemStack.getItem() instanceof ItemArmor))
            {
                continue;
            }
            final ItemArmor armor = (ItemArmor) itemStack.getItem();
            final float armorScore = getArmorScore(itemStack, armor);
            final int armorIndex = 3 - armor.armorType;
            if (armorScore > equippedArmorScores[armorIndex])
            {
                equippedArmorScores[armorIndex] = armorScore;
                armorPieces[armorIndex] = i;
                shouldReplaceArmor = true;
            }
        }
        return shouldReplaceArmor;
    }

    private float getArmorScore(final ItemStack itemStack, final ItemArmor armor)
    {
        float score = armor.damageReduceAmount;
        for (final Enchantment enchantment : RELEVANT_ENCHANTMENTS)
        {
            float enchantmentLevel = ItemUtil.getEnchantLevel(enchantment, itemStack);
            if (enchantmentLevel == 0.0f)
            {
                continue;
            }
            if (enchantment.effectId == Enchantment.thorns.effectId
                    && noThornsSetting.getValue())
            {
                enchantmentLevel *= 0.25f;
            }
            score += enchantmentLevel;
        }
        return score;
    }
}
