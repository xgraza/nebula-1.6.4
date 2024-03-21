package nebula.client.account;

import io.sentry.Sentry;
import nebula.client.Nebula;
import nebula.client.config.Config;
import nebula.client.util.fs.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.StringJoiner;

/**
 * @author Gavin
 * @since 08/13/23
 */
public class AccountConfig implements Config {
  @Override
  public void save() {
    StringJoiner joiner = new StringJoiner("\n");

    for (Account account : Nebula.INSTANCE.account.values()) {
      joiner.add(account.username());
    }

    try {
      FileUtils.writeFile(file(), joiner.toString());
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

    for (String line : content.split("\n")) {
      line = line.trim();
      if (line.isEmpty()) continue;
      Nebula.INSTANCE.account.add(new Account(line));
    }
  }

  @Override
  public File file() {
    return new File(FileUtils.ROOT, "accounts.txt");
  }
}
