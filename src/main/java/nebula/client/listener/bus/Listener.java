/* chronos.dev - Aesthetical © 2023 */
package nebula.client.listener.bus;

/**
 * @author aesthetical
 * @since 06/11/23
 */
@FunctionalInterface
public interface Listener<T> {
  void invoke(T event);
}
