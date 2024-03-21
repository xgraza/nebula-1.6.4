package nebula.client.friend;

/**
 * @author Gavin
 * @since 08/13/23
 */
public class Friend {

  private final String username;
  private String alias;

  public Friend(String username) {
    this.username = username;
  }

  public String username() {
    return username;
  }

  public String alias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }
}
