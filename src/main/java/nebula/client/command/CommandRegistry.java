package nebula.client.command;

import io.sentry.Sentry;
import nebula.client.Nebula;
import nebula.client.command.exception.CommandFailureException;
import nebula.client.command.impl.*;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.net.EventPacket;
import nebula.client.util.chat.Printer;
import nebula.client.util.registry.Registry;
import nebula.client.util.system.Reflections;
import net.minecraft.network.play.client.C01PacketChatMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Gavin
 * @since 08/10/23
 */
public class CommandRegistry implements Registry<Command> {

  /**
   * The package containing the command implementations
   */
  public static final String COMMAND_IMPL = "nebula.client.command.impl";

  /**
   * The logger for the registry
   */
  private static final Logger log = LogManager.getLogger("Commands");

  private final Map<String, Command> commandAliasMap = new HashMap<>();
  private final Set<Command> commands = new LinkedHashSet<>();

  @Override
  public void init() {
    Nebula.BUS.subscribe(this);

//    try {
//      Reflections.classesInPackage(COMMAND_IMPL, Command.class)
//        .forEach((clazz) -> {
//          try {
//            add((Command) clazz.getConstructors()[0].newInstance());
//          } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
//            log.error("Failed to invoke constructor on {}", clazz);
//            e.printStackTrace();
//            Sentry.captureException(e);
//          }
//        });
//    } catch (Exception e) {
//      e.printStackTrace();
//      Sentry.captureException(e);
//    }

    {
      // add(new ChunkSizeCommand());
      add(new DupeBookCommand());
      add(new FakePlayerCommand());
      add(new HideCommand());
      add(new ItemSpawnCommand());
      add(new SelfKickCommand());
      add(new SpawnTPCommand());
      add(new VClipCommand());
    }

    log.info("Discovered {} modules from {}", commands.size(), COMMAND_IMPL);
  }

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event -> {
    if (event.packet() instanceof C01PacketChatMessage packet) {
      String message = packet.func_149439_c();
      if (!message.startsWith(".")) return;

      event.setCanceled(true);

      String[] args = message.substring(1)
        .trim().split("\\s+");
      if (args.length == 0) return;

      Command command = commandAliasMap.get(args[0].toLowerCase());
      if (command == null) {

        final String argument = args[0];
        if (argument.equalsIgnoreCase("help")) {
          Printer.print("Soon :tm:");
        }

        return;
      }

      try {
        int result = command.execute(Arrays.copyOfRange(args, 1, args.length));
        switch (result) {
          case CommandResults.SUCCESS -> Printer.print("Command was dispatched successfully.");
          case CommandResults.INVALID_SYNTAX -> {
            String syntax = command.meta().syntax();
            if (syntax == null || syntax.isEmpty()) return;
            Printer.print("Invalid syntax. Syntax: " + syntax);
          }
        }
      } catch (CommandFailureException e) {
        Printer.print(e.getMessage());
      } catch (Exception e) {
        e.printStackTrace();
        Sentry.captureException(e);
      }
    }
  };

  @Override
  public void add(Command... elements) {
    for (Command element : elements) {
      commands.add(element);
      for (String alias : element.meta().aliases()) {
        commandAliasMap.put(alias.toLowerCase(), element);
      }
    }
  }

  @Override
  public void remove(Command... elements) {

  }

  @Override
  public Collection<Command> values() {
    return commands;
  }
}
