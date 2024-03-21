package nebula.client.command.exception;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class CommandFailureException extends Exception {
  private final String reason;

  public CommandFailureException(String reason) {
    this.reason = reason;
  }

  @Override
  public String getMessage() {
    return reason;
  }
}
