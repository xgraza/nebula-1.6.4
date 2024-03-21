package nebula.client.account;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

/**
 * @author Gavin
 * @since 08/13/23
 * @param username the username of the cracked account
 */
public record Account(String username) {

  public void login() {
    Minecraft.getMinecraft().setSession(new Session(username, "0", "legacy"));
  }
}
