package nebula.client.command;

/**
 * @author Gavin
 * @since 08/10/23
 */
public interface CommandResults {
  int SUCCESS = 0;
  int SUCCESS_NO_RESPONSE = 1;
  int INVALID_SYNTAX = 2;
}
