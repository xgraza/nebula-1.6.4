package nebula.client.util.system;

import java.util.function.Supplier;

/**
 * @author Gavin
 * @since 08/17/23
 * @param <T> the type
 */
public class Reference<T> {
  private T value;
  private final Supplier<T> supplier;

  public Reference(Supplier<T> supplier) {
    this.supplier = supplier;
  }

  public T get() {
    if (value == null) {
      value = supplier.get();
    }
    return value;
  }
}
