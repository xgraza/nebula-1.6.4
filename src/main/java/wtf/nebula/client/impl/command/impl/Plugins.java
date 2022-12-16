package wtf.nebula.client.impl.command.impl;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.event.impl.network.EventPacket;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Plugins extends Command {
    private static final Pattern PLUGIN_REGEX = Pattern.compile("[A-Z]\\w+");

    private boolean await = false;

    public Plugins() {
        super(new String[]{"plugins", "serverplugins"});
        Nebula.BUS.subscribe(this);
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S3APacketTabComplete && await) {
            await = false;

            S3APacketTabComplete packet = event.getPacket();

            String[] results = packet.func_149630_c();

            if (results == null || results.length == 0) {
                print("No plugins found :(");
            } else {
                Set<String> plugins = new HashSet<>();

                for (String result : results) {
                    if (result.matches(PLUGIN_REGEX.pattern()) && !result.startsWith("/")) {
                        plugins.add(result);
                    } else {
                        if (result.contains(":")) {
                            String[] split = result.split(":");
                            String pluginName = split[0]
                                    .replaceAll("\\s*", "")
                                    .replace("/", "");

                            if (!pluginName.isEmpty() && !pluginName.equals("minecraft")) {
                                plugins.add(pluginName);
                            }
                        }
                    }
                }

                if (!plugins.isEmpty()) {
                    print("Found " + plugins.size() + " plugins: " + String.join(", ", plugins));
                } else {
                    print("This is probably a vanilla server or no plugins could be parsed.");
                }
            }
        }
    }

    @Override
    public String dispatch(CommandContext ctx) {
        await = true;

        String method = "default";
        if (!ctx.getRawArgs().isEmpty()) {
            method = ctx.getRawArgs().get(0).toLowerCase();
        }

        String tabCompleteMessage;

        switch (method.toLowerCase()) {
            case "default":
                tabCompleteMessage = "/";
                break;

            case "help":
                tabCompleteMessage = "/help ";
                break;

            default:
                tabCompleteMessage = "";
                break;
        }

        mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(tabCompleteMessage));

        return "Trying to find plugins...";
    }
}
