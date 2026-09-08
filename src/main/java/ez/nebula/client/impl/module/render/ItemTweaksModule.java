package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.text.FormattingUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.StatCollector;

/**
 * @author xgraza
 * @since 3/20/26
 */
@ModuleManifest(name = "ItemTweaks",
        description = "Tweaks how an item is rendered in the GUI",
        category = ModuleCategory.RENDER)
public final class ItemTweaksModule extends Module
{
    @ModuleInstance
    public static ItemTweaksModule INSTANCE;

    private final Setting<Boolean> trueDurabilitySetting = builder("True Durability", false)
            .setDescription("If to show an item's true durability, negative or not")
            .build();
    private final Setting<Boolean> blockIDSetting = builder("Show Block IDs", true)
            .setDescription("If to show a blocks ID (+subid) when hovering")
            .build();
    private final Setting<Boolean> infiniteSetting = builder("Show Infinite Items", true)
            .setDescription("If to show the true item size on items")
            .build();
    private final Setting<Boolean> showItemSizeSetting = builder("Show Item Size", true)
            .setDescription("If to show the size in bytes of an item when hovering over it")
            .build();
    private final Setting<Boolean> showContainerSizeSetting = builder("Show Container Size", false)
            .setDescription("If to show the size of a container")
            .build();
    private final Setting<Boolean> formatEnchantmentsSetting = builder("Format Enchants", true)
            .setDescription("If to format all enchantments")
            .build();

    public String formatEnchantment(final Enchantment enchantment, int level)
    {
        final String localizedEnchant = StatCollector.translateToLocal(enchantment.getName());
        if (level == Short.MAX_VALUE)
        {
            return localizedEnchant + " 32k";
        } else if (level == 128 || level == 127)
        {
            return localizedEnchant + " " + level;
        } else if (level <= 0)
        {
            return localizedEnchant;
        }
        return localizedEnchant + " " + FormattingUtil.formatRomanNumeral(level);
    }

    public boolean showBlockIDs()
    {
        boolean advancedItemTooltips = MC.gameSettings.advancedItemTooltips;
        if (isToggled())
        {
            return blockIDSetting.getValue() || advancedItemTooltips;
        }
        return advancedItemTooltips;
    }

    public boolean trueDurability()
    {
        return isToggled() && trueDurabilitySetting.getValue();
    }

    public boolean showItemSize()
    {
        return isToggled() && showItemSizeSetting.getValue();
    }

    public boolean showInfinites()
    {
        return isToggled() && infiniteSetting.getValue();
    }

    public boolean showContainerSize()
    {
        return isToggled() && showContainerSizeSetting.getValue();
    }

    public boolean formatEnchantLevels()
    {
        return isToggled() && formatEnchantmentsSetting.getValue();
    }
}
