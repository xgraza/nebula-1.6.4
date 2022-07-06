package wtf.nebula.repository.impl;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.src.Packet3Chat;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.command.Command;
import wtf.nebula.impl.command.impl.*;
import wtf.nebula.repository.BaseRepository;
import wtf.nebula.repository.Repositories;
import wtf.nebula.repository.Repository;
import wtf.nebula.util.FileUtil;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Repository("Commands")
public class CommandRepository extends BaseRepository<Command> {
    public final Map<String, Command> commandByTrigger = new HashMap<>();

    private String prefix = "+";
    private boolean introduction = false;

    @Override
    public void init() {
        addChild(new Bind());
        addChild(new BookBot());
        addChild(new Friend());
        addChild(new Github());
        addChild(new Help());
        addChild(new Prefix());
        addChild(new Search());
        addChild(new SpammerFile());
        addChild(new Spawn());
        addChild(new Throw());
        addChild(new Toggle());
        addChild(new Waypoints());
        addChild(new Write());
        addChild(new Xray());

        log.logInfo("Loaded " + children.size() + " commands.");

        if (!Files.exists(FileUtil.COMMAND_PREFIX)) {
            save(prefix);
            introduction = true;
        }

        else {
            String text = FileUtil.read(FileUtil.COMMAND_PREFIX);
            if (text != null && !text.isEmpty()) {
                prefix = text.trim().replaceAll("\n", "").replaceAll("\r\n", "");
            }
        }
    }

    @Override
    public void addChild(Command child) {
        childMap.put(child.getClass(), child);
        children.add(child);
        child.getTriggers().forEach((trigger) -> commandByTrigger.put(trigger, child));
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet3Chat) {
            Packet3Chat packet = event.getPacket();
            if (packet.message.startsWith(prefix)) {
                event.setCancelled(true);
                handle(packet.message);
            }
        }
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (introduction) {
            introduction = false;
            sendChatMessage("Hey there! You seem to be launching the client for the first time. Your command prefix is " + prefix + ". You can open the ClickGUI by pressing right shift on your keyboard! Happy hacking");
        }
    }

    private void handle(String message) {
        String[] args = message.substring(prefix.length())
                .trim()
                .split(" ");

        if (args.length == 0) {
            return;
        }

        Command command = commandByTrigger.getOrDefault(args[0], null);
        if (command != null) {
            try {
                command.execute(Arrays.asList(args).subList(1, args.length));
            } catch (Exception e) {
                sendChatMessage("This command failed to execute. Check the logs and report on the Github repo.");
                e.printStackTrace();
            }
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        save(prefix);
    }

    public String getPrefix() {
        return prefix;
    }

    public static void save(String prefix) {
        FileUtil.write(FileUtil.COMMAND_PREFIX, prefix);
    }

    public static CommandRepository get() {
        return Repositories.getRepo(CommandRepository.class);
    }
}
