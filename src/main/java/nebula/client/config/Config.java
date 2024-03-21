package nebula.client.config;

import java.io.File;

/**
 * @author Gavin
 * @since 08/09/23
 */
public interface Config {

  void save();
  void load();

  File file();
}
