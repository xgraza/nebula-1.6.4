package nebula.client.util.fs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * @author Gavin
 * @since 08/09/23
 */
public class JSONUtils {

  public static final Gson GSON = new GsonBuilder()
      .setPrettyPrinting()
      .serializeNulls()
      .serializeSpecialFloatingPointValues()
      .create();

  public static String json(JsonElement element) {
    return GSON.toJson(element);
  }

  public static <T extends JsonElement> T parse(String buffer, Class<T> clazz) {
    return GSON.fromJson(buffer, clazz);
  }
}
