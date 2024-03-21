/* chronos.dev - Aesthetical © 2023 */
package nebula.client.util.system;

import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Gavin
 * @since 08/09/23
 */
@SuppressWarnings("unchecked")
public class Reflections {

  /**
   * Gets all classes inside of a java package
   * @param packageName the name of the package
   * @param type the class type
   * @return a {@link LinkedHashSet} of the classes if any were found
   * @param <T> the type of class to find
   * @throws IOException if the package could not be read from the system classloader
   */
  public static <T> Set<Class<? extends T>> classesInPackage(
    String packageName,
    Class<T> type
  ) throws IOException {
    //LogManager.getLogger("test").info(packageName.replaceAll("\\.", "/"));
    InputStream is = Reflections.class.getClassLoader()
      .getResourceAsStream(packageName.replaceAll("\\.", "/"));
    if (is == null) {
      return Collections.emptySet();
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    Set<Class<? extends T>> classes = new LinkedHashSet<>();

    String next;
    while ((next = reader.readLine()) != null) {
      if (next.endsWith(".class")) {
        Class<?> clazz = fromName(packageName, next);
        if (clazz != null
          && type.isAssignableFrom(clazz))
          classes.add((Class<? extends T>) clazz);
      } else {
        // package
        if (next.contains(".")) continue;

        // recursively get the classes here
        classes.addAll(classesInPackage(
          packageName + "." + next, type));
      }
    }

    return classes;
  }

  private static Class<?> fromName(String packageName, String name) {
    if (!packageName.endsWith(".")) packageName += ".";
    packageName = packageName.replaceAll("/", ".");

    try {
      return Class.forName(
        packageName
          + name.replaceAll("\\.class", ""));
    } catch (ClassNotFoundException ignored) {}

    return null;
  }
}
