package nebula.client.command;

/**
 * @author Gavin
 * @since 08/10/23
 */
public interface CommandExecutor {

  /**
   * Executes a command
   * @param args the arguments
   * @return the result of the command execution
   */
  int execute(String[] args) throws Exception;
}
