package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C01PacketChatMessage;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.io.FileUtils;
import wtf.nebula.client.utils.player.CharacterUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spammer extends ToggleableModule {
    public static final File SPAMMER_FOLDER = new File(FileUtils.CLIENT_DIRECTORY, "spammer");
    private static File file;

    private static List<String> lines;
    private static int index = 0;

    private final Property<Integer> delay = new Property<>(5, 0, 20, "Delay", "dl");
    private static final Property<String> fileName = new Property<>("File Name", "");

    private final Timer timer = new Timer();

    public Spammer() {
        super("Spammer", new String[]{"spam", "autospam", "annoyerv2"}, ModuleCategory.MISCELLANEOUS);
        offerProperties(delay, fileName);

        if (!SPAMMER_FOLDER.exists()) {
            SPAMMER_FOLDER.mkdir();
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        lines = null;
        index = 0;
    }

    @EventListener
    public void onTick(EventTick event) {

        if (file == null || !file.exists() || lines == null) {
            if (fileName.getValue() != null && !fileName.getValue().isEmpty()) {
                File f = new File(SPAMMER_FOLDER, fileName.getValue());
                if (f.exists() && !f.isDirectory()) {
                    setFile(f);
                    return;
                }
            }

            print("Invalid spammer file. Use the .spammerfile command.");
            setRunning(false);
            return;
        }

        if (!lines.isEmpty() && timer.hasPassed(delay.getValue() * 1000L, false)) {

            try {

                String line = lines.get(index);
                if (line == null || line.isEmpty()) {
                    ++index;
                    return;
                }

                // remove any trailing whitespaces / illegal characters
                line = line.trim();

                // if it still contains illegal characters, remove
                if (CharacterUtils.containsIllegalCharacters(line)) {
                    ++index;
                    return;
                }

                mc.thePlayer.sendQueue.addToSendQueueSilent(new C01PacketChatMessage(line));

                ++index;
                timer.resetTime();

            } catch (Exception e) {
                e.printStackTrace();
                index = 0;
            }
        }
    }

    private static void reset() {
        index = 0;
        lines = new ArrayList<>();

        String content = FileUtils.read(file);
        if (content == null || content.isEmpty()) {
            lines = null;
            return;
        }

        lines = Arrays.asList(content.trim().split("\n"));
    }

    public static void setFile(File file) {
        Spammer.file = file;
        fileName.setValue(file.getName());
        reset();
    }
}
