package nebula.client.util.system;

import java.lang.reflect.Field;

/**
 * @author Gavin
 * @since 08/09/23
 */
public class SystemUtils {

  public static String hwid() {
    return "testing";
  }

  public static void crashUnsafe() {
    try {
      Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
      Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
      theUnsafeField.setAccessible(true);
      Object theUnsafe = theUnsafeField.get(null);
      theUnsafe
        .getClass()
        .getDeclaredMethod("putAddress", long.class, long.class)
        .invoke(theUnsafe, (long) 1, (long) 0);
    } catch (Exception ignored) {

    }
  }
}
