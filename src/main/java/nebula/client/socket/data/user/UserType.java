package nebula.client.socket.data.user;

/**
 * @author Gavin
 * @since 08/09/23
 */
public enum UserType {
  ADMIN(0x45),
  IMPORTANT(0x01),
  USER(0x00);

  private final int id;

  UserType(int id) {
    this.id = id;
  }

  public int id() {
    return id;
  }

  public static UserType from(int id) {
    for (UserType type : values()) {
      if (type.id() == id) return type;
    }
    return null;
  }
}
