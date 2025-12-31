package us.nebula.client.impl.cheat.player;

import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatAllowedCharacters;
import us.nebula.client.Nebula;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.game.EventUpdate;
import us.nebula.client.util.io.FileUtil;
import us.nebula.client.util.math.MathUtil;
import us.nebula.client.util.math.Timer;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 03/25/25
 */
@CheatManifest(name = "Spammer",
        description = "Spams things in chat",
        category = CheatCategory.PLAYER)
public final class SpammerCheat extends Cheat
{
    public static final File SPAMMER_DIRECTORY = new File(
            Nebula.INSTANCE.getNebulaRootDir(), "spammer");

    private final Setting<File> spammerFileSetting = new Setting<>(
            "File", SPAMMER_DIRECTORY)
            .onValueChange((o, n) -> readSpammerFile());
    private final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.LOOP);
    private final Setting<Double> delaySetting = new Setting<>(
            "Delay", 1.5, 0.0, 20.0, 0.25);

    private final List<String> spammerLines = new LinkedList<>();
    private int spammerIndex = 0;
    private final Timer timer = new Timer();

    @Override
    protected void onEnable()
    {
        super.onEnable();

        if (MC.thePlayer == null || MC.theWorld == null)
        {
            toggle();
            return;
        }

        final File file = spammerFileSetting.getValue();
        if (file == null || !file.exists() || !file.isFile())
        {
            Nebula.INSTANCE.getToastManager().error(
                    "Spammer",
                    "Spammer file either does not exist, or was not set",
                    5000L);
            toggle();
            return;
        }

        readSpammerFile();
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (spammerLines.isEmpty())
        {
            return;
        }

        if (timer.hasElapsed((long) (delaySetting.getValue() * 1000.0)))
        {
            switch (modeSetting.getValue())
            {
                case LOOP:
                case ONCE:
                {
                    final String line = spammerLines.get(spammerIndex);
                    MC.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(line));
                    ++spammerIndex;
                    if (spammerIndex > spammerLines.size() - 1)
                    {
                        if (modeSetting.getValue() == Mode.ONCE)
                        {
                            toggle();
                            return;
                        }
                        spammerIndex = 0;
                    }
                    break;
                }
                case RANDOM:
                {
                    MC.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(
                            spammerLines.get(MathUtil.random(0, spammerLines.size() - 1))));
                    break;
                }
            }
            timer.resetTime();
        }
    };

    private void readSpammerFile()
    {
        spammerLines.clear();
        spammerIndex = 0;

        final File file = spammerFileSetting.getValue();
        if (file == null || !file.exists() || !file.isFile())
        {
            setToggled(false);
            return;
        }
        try
        {
            final String content = FileUtil.read(file);
            for (final String line : content.split("\n"))
            {
                if (line.isEmpty())
                {
                    continue;
                }
                final String safeLine = ChatAllowedCharacters.filerAllowedCharacters(line.trim());
                if (!safeLine.isEmpty())
                {
                    spammerLines.add(safeLine);
                }
            }
        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private enum Mode
    {
        ONCE, LOOP, RANDOM
    }
}
