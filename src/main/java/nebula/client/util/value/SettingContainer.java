package nebula.client.util.value;

import java.util.Collection;

/**
 * @author Gavin
 * @since 08/09/23
 */
public interface SettingContainer {
  void init();
  Setting<?> get(String name);
  Collection<Setting<?>> settings();
}
