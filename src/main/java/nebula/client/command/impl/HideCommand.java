package nebula.client.command.impl;

import nebula.client.Nebula;
import nebula.client.command.Command;
import nebula.client.command.CommandMeta;
import nebula.client.command.CommandResults;
import nebula.client.command.exception.CommandFailureException;
import nebula.client.module.Module;

/**
 * @author Gavin
 * @since 08/25/23
 */
@SuppressWarnings("unused")
@CommandMeta(aliases = {"hide", "show", "drawn"},
  description = "Hide a module from the arraylist",
  syntax = "[module name]")
public class HideCommand extends Command {
  @Override
  public int execute(String[] args) throws Exception {

    if (args.length == 0) return CommandResults.INVALID_SYNTAX;

    Module module = null;
    for (Module m : Nebula.INSTANCE.module.values()) {
      if (m.meta().name().toLowerCase().startsWith(args[0].toLowerCase())) {
        module = m;
        break;
      }
    }

    if (module == null) throw new CommandFailureException(
      "No module found with that name");

    module.setHidden(!module.hidden());
    return CommandResults.SUCCESS;
  }
}
