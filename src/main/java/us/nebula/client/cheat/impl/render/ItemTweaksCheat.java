package us.nebula.client.cheat.impl.render;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.StatCollector;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.setting.Setting;
import us.nebula.client.util.FormattingUtil;

/**
 * @author xgraza
 * @since 3/20/26
 */
@CheatManifest(name = "ItemTweaks",
        description = "Tweaks how an item is rendered in the GUI",
        category = CheatCategory.RENDER)
public final class ItemTweaksCheat extends Cheat
{
    @CheatInstance
    public static ItemTweaksCheat INSTANCE;

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
