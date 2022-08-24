package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C01PacketChatMessage;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.repository.impl.ModuleRepository;
import wtf.nebula.util.FileUtil;
import wtf.nebula.util.Timer;

import java.awt.*;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Spammer extends Module {
    public Spammer() {
        super("Spammer", ModuleCategory.MISC);
    }

    public final Value<Integer> delay = new Value<>("Delay", 5, 0, 20);
    public final Value<Boolean> loop = new Value<>("Loop", true);
    public final Value<String> fileName = new Value<>("FileName", null);

    private final Timer timer = new Timer();
    private List<String> lines = null;
    private int index = 0;

    @Override
    protected void onActivated() {
        super.onActivated();

        if (!Files.exists(FileUtil.SPAMMER)) {
            try {
                Files.createDirectory(FileUtil.SPAMMER);

                if (!nullCheck()) {
                    sendChatMessage("Created spammer folder. Opened in your file explorer");
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.OPEN)) {
                        Desktop.getDesktop().open(FileUtil.SPAMMER.toFile());
                    }

                    else {
                        sendChatMessage("bro wtf ur de/wm is not supporting my file explorer opening wtf mane");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (fileName.getValue() != null) {
            start(fileName.getValue());
        }
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        lines = null;
        index = 0;
    }

    @EventListener
    public void onTick(TickEvent event) {

        if (lines != null && !lines.isEmpty()) {

            if (timer.passedTime(delay.getValue().longValue() * 1000L, false)) {
                String str = lines.get(index);
                if (str == null || str.isEmpty()) {
                    return;
                }

                timer.resetTime();

                ++index;
                if (index >= lines.size()) {
                    index = 0;

                    if (!loop.getValue()) {
                        setState(false);
                        return;
                    }
                }

                mc.thePlayer.sendQueue.addToSendQueueSilent(new C01PacketChatMessage(str));
            }
        }
    }

    public void start(String name) {
        if (!ModuleRepository.get().getModule(Spammer.class).getState()) {
            return;
        }

        Path file = FileUtil.SPAMMER.resolve(name);
        if (!Files.exists(file) || !Files.isReadable(file)) {
            sendChatMessage("Unable to read file.");
            return;
        }

        lines = Arrays.asList(FileUtil.read(file).trim().split("\n"));
        timer.resetTime();
        index = 0;
    }
}
