package nebula.client.socket.data.user;

import com.google.gson.JsonObject;

/**
 * @author Gavin
 * @since 08/09/23
 */
public record NebulaUser(UserType type) {

  public static NebulaUser from(JsonObject data) {
    UserType type = UserType.from(data.get("userType").getAsInt());
    return new NebulaUser(type);
  }
}
