package nebula.client.config;

import io.sentry.Sentry;
import nebula.client.Nebula;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Gavin
 * @since 08/09/23
 */
public final class ConfigLoader {

  private static final List<Config> configs = new LinkedList<>();

  /**
   * Adds a config
   * @param config the config object
   */
  public static void add(Config config) {
    configs.add(config);
  }

  /**
   * Loads the configs
   */
  public static void load() {
    Nebula.EXECUTOR.execute(() -> {
      for (Config config : configs) {
        try {
          config.load();
        } catch (Exception e) {
          e.printStackTrace();
          Sentry.captureException(e);
        }
      }
    });
  }

  /**
   * Saves the configs
   */
  public static void save() {
    for (Config config : configs) {
      try {
        config.save();
      } catch (Exception e) {
        e.printStackTrace();
        Sentry.captureException(e);
      }
    }
  }
}
