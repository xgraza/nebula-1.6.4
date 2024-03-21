package nebula.client.util.registry;

import java.util.Collection;

/**
 * @author Gavin
 * @since 08/09/23
 * @param <T> the type this registry holds
 */
public interface Registry<T> {

  void init();

  void add(T... elements);
  void remove(T... elements);

  Collection<T> values();
}
