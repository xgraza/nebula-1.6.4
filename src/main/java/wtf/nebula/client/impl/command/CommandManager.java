package wtf.nebula.client.impl.command;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import wtf.nebula.client.api.config.Config;
import wtf.nebula.client.api.registry.BaseRegistry;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.command.arg.Argument;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.command.impl.*;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.input.EventKeyInput;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.utils.io.FileUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CommandManager extends BaseRegistry<Command> {
    public static final int COMMAND_ID = -1337;
    public final Map<String, Command> commandNameMap = new HashMap<>();

    public String commandPrefix = ".";

    public CommandManager() {
        Nebula.BUS.subscribe(this);

        add(new Blocks.Xray());
        add(new Configs());
        add(new Drawn());
        add(new FakePlayer());
        add(new Friend());
        add(new Help());
        add(new Packets());
        add(new Plugins());
        add(new Prefix());
        add(new SpammerFile());
        add(new SpawnTP());
        add(new Toggle());
        add(new VClip());
        add(new HClip());
        add(new Watermark());
        add(new Waypoints());

        new Config("command_prefix.txt") {
            @Override
            public void load(String element) {
                if (element == null || element.isEmpty()) {
                    commandPrefix = ".";
                } else {
                    commandPrefix = element.trim()
                            .replaceAll("\n", "")
                            .replaceAll("\n\r", "");
                }
            }

            @Override
            public void save() {
                FileUtils.write(getFile(), commandPrefix);
            }
        };
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getEra().equals(Era.PRE) && event.getPacket() instanceof C01PacketChatMessage) {
            C01PacketChatMessage packet = event.getPacket();
            String m = packet.message;

            if (m == null || m.isEmpty() || !m.startsWith(commandPrefix)) {
                return;
            }

            event.setCancelled(true);

            String[] a = m
                    .trim()
                    .substring(commandPrefix.length())
                    .split(" (?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");

            Command command = commandNameMap.getOrDefault(a[0].toLowerCase(), null);
            if (command == null) {
                print("Invalid command entered.", COMMAND_ID);
            } else {

                try {
                    List<String> args = new ArrayList<>(Arrays.asList(a));
                    args.remove(0);

                    CommandContext ctx = new CommandContext(m, args);

                    Argument[] cmdArgs = command.getArgs();
                    if (cmdArgs.length > 0) {
                        int requiredArguments = 0;
                        for (Argument arg : cmdArgs) {
                            if (arg.isRequired()) {
                                requiredArguments += 1;
                            }
                        }

                        if (args.size() < requiredArguments) {

                            String syntax = commandPrefix + "[" + String.join("|", command.getAliases()) + "] " + Arrays.stream(cmdArgs).map((arg) -> {
                                String s = "";

                                if (arg.isRequired()) {
                                    s += ("[");
                                } else {
                                    s += ("(");
                                }

                                s += (arg.getName());

                                if (arg.isRequired()) {
                                    s += ("]");
                                } else {
                                    s += (")");
                                }

                                return EnumChatFormatting.YELLOW + s + EnumChatFormatting.GRAY;
                            }).collect(Collectors.joining(" "));

//                            for (Argument arg : cmdArgs) {
//                                syntax.append(EnumChatFormatting.YELLOW + " ");
//
//                                if (arg.isRequired()) {
//                                    syntax.append("[");
//                                } else {
//                                    syntax.append("(");
//                                }
//
//                                syntax.append(arg.getName());
//
//                                if (arg.isRequired()) {
//                                    syntax.append("]");
//                                } else {
//                                    syntax.append(")");
//                                }
//
//                                syntax.append(EnumChatFormatting.GRAY + " ");
//                            }

                            print(syntax, COMMAND_ID);
                            return;
                        }

                        if (!args.isEmpty()) {

                            for (int i = 0; i < cmdArgs.length; ++i) {
                                Argument arg = cmdArgs[i];

                                try {
                                    String content = args.get(i);
                                    if (content.isEmpty()) {
                                        print("Missing argument " + EnumChatFormatting.YELLOW + arg.getName() + EnumChatFormatting.GRAY + ".", COMMAND_ID);
                                        return;
                                    }

                                    content = content.trim().replaceAll("\"", "");

                                    boolean parsed = arg.parse(content);
                                    if (!parsed && arg.isRequired()) {
                                        print("Invalid argument value provided at " + EnumChatFormatting.YELLOW + arg.getName() + EnumChatFormatting.GRAY + ".", COMMAND_ID);
                                    }

                                    arg.setLastParseResult(parsed);

                                    ctx.putArg(arg.getName(), arg);
                                } catch (IndexOutOfBoundsException e) {
                                    print("Invalid parse at argument " + EnumChatFormatting.YELLOW + arg.getName() + EnumChatFormatting.GRAY + ".", COMMAND_ID);
                                }
                            }
                        }
                    }

                    try {
                        print(command.dispatch(ctx), COMMAND_ID);
                    } catch (Exception e) {
                        print("Command failed to dispatch", COMMAND_ID);

                        e.printStackTrace();
                    }

                    Arrays.stream(command.getArgs()).forEach((arg) -> arg.setValue(null));
                } catch (Exception e) {
                    Nebula.LOGGER.error("Failed to parse command");

                    print("Invalid command parse.", COMMAND_ID);

                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void add(Command instance) {
        super.add(instance);
        Arrays.stream(instance.getAliases()).forEach((alias) -> commandNameMap.put(alias, instance));
    }
}
