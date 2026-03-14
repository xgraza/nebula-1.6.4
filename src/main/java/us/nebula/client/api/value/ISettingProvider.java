package us.nebula.client.api.value;

import java.util.List;

/**
 * @author xgraza
 * @since 02/16/25
 */
public interface ISettingProvider
{
    List<Setting<?>> getSettings();

    <T> Setting<T> getSetting(final String name);

    void reflectSettings();
}
