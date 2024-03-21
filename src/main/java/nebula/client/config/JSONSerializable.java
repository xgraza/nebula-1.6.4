package nebula.client.config;

import com.google.gson.JsonElement;

/**
 * @author Gavin
 * @since 08/09/23
 */
public interface JSONSerializable {

  /**
   * Saves this object to a json element
   * @return the json element
   */
  JsonElement save();

  /**
   * Loads this object from a json element
   * @param element the json element
   */
  void load(JsonElement element);
}
