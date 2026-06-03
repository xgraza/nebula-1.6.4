package ez.nebula.client.api.setting;

import ez.nebula.client.api.setting.block.BlockSetting;
import net.minecraft.block.Block;

import java.awt.Color;
import java.util.List;

/**
 * @author xgraza
 * @since 1/27/26
 */
public interface SettingProvider
{
    void discoverSettings();

    void registerSetting(final Setting<?> setting);

    <T> Setting<T> getSetting(final String name);

    List<Setting<?>> getSettings();

    default <T> Setting.Builder<T> builder(final String name, final T value)
    {
        return new Setting.Builder<T>(name, value);
    }

    default <T extends Enum<T>> EnumSetting.Builder<T> enumBuilder(final String name, final T value)
    {
        return new EnumSetting.Builder<>(name, value);
    }

    default <T extends Number> NumberSetting.Builder<T> numberBuilder(final String name, final T value)
    {
        return new NumberSetting.Builder<>(name, value);
    }

    default ColorSetting.Builder colorBuilder(final String name, final Color value)
    {
        return new ColorSetting.Builder(name, value);
    }

    default BlockSetting.Builder blockBuilder(final String name, final Block block)
    {
        return new BlockSetting.Builder(name, block);
    }
}