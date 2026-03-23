package us.nebula.client.api.value;

import us.nebula.client.ClientSettings;
import us.nebula.client.Nebula;
import us.nebula.client.api.trait.DebugFeature;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author xgraza
 * @since 02/16/25
 */
public interface ISettingProvider
{
    void addSetting(Setting<?> setting);

    List<Setting<?>> getSettings();

    <T> Setting<T> getSetting(final String name);

    default void reflectSettings()
    {
        for (final Field field : getClass().getDeclaredFields())
        {
            if (!Setting.class.isAssignableFrom(field.getType()))
            {
                continue;
            }

            if (field.isAnnotationPresent(DebugFeature.class) && !ClientSettings.DEBUG)
            {
                continue;
            }

            field.setAccessible(true);
            try
            {
                addSetting((Setting<?>) field.get(this));
            } catch (final IllegalAccessException e)
            {
                Nebula.INSTANCE.getLogger().error(
                        "Failed to reflect setting from {}", this);
                Nebula.INSTANCE.getLogger().error(e);
            }
        }
    }
}
