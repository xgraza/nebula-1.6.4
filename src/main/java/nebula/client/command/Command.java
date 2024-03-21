package nebula.client.command;

import io.sentry.Sentry;
import net.minecraft.client.Minecraft;

/**
 * @author Gavin
 * @since 08/09/23
 */
public abstract class Command implements CommandExecutor {

  /**
   * The minecraft game instance
   */
  protected final Minecraft mc = Minecraft.getMinecraft();

  private final CommandMeta meta;

  public Command() {
    if (!getClass().isAnnotationPresent(CommandMeta.class)) {
      RuntimeException e = new RuntimeException("@CommandMeta needs to be at the top of " + getClass());
      Sentry.captureException(e);
      throw e;
    }

    meta = getClass().getDeclaredAnnotation(CommandMeta.class);
  }

  public CommandMeta meta() {
    return meta;
  }
}
