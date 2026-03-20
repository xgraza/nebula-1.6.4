package us.nebula.client.impl.cheat.render;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.StatCollector;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatInstance;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
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

    private final Setting<Boolean> infiniteSetting = new Setting<>(
            "Show Infinite Items", true);
    private final Setting<Boolean> showItemSizeSetting = new Setting<>(
            "Show Item Size", true);
    private final Setting<Boolean> showContainerSizeSetting = new Setting<>(
            "Show Container Size", false);
    private final Setting<Boolean> formatEnchantmentsSetting = new Setting<>(
            "Format Enchants", true);

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
