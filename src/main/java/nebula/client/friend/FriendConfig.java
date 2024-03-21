package nebula.client.friend;

import io.sentry.Sentry;
import nebula.client.Nebula;
import nebula.client.config.Config;
import nebula.client.util.fs.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Gavin
 * @since 08/13/23
 */
public class FriendConfig implements Config {
  @Override
  public void save() {

    StringBuilder builder = new StringBuilder();
    for (Friend friend : Nebula.INSTANCE.friend.values()) {
      builder.append(friend.username());
      if (friend.alias() != null && !friend.alias().isEmpty()) {
        builder.append(",").append(friend.alias());
      }

      builder.append("\n");
    }

    try {
      FileUtils.writeFile(file(), builder.toString());
    } catch (IOException e) {
      e.printStackTrace();
      Sentry.captureException(e);
    }
  }

  @Override
  public void load() {
    if (!file().exists()) return;

    String content;
    try {
      content = FileUtils.readFile(file());
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    if (content.isEmpty()) return;

    Nebula.INSTANCE.friend.clear();

    for (String line : content.split("\n")) {
      line = line.trim();
      if (line.isEmpty()) continue;

      String[] parts = line.split(",");
      Friend friend = new Friend(parts[0]);
      if (parts.length == 2) friend.setAlias(parts[1]);

      Nebula.INSTANCE.friend.add(friend);
    }
  }

  @Override
  public File file() {
    return new File(FileUtils.ROOT, "friends.txt");
  }
}
