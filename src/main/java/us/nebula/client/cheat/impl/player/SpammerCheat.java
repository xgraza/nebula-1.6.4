package us.nebula.client.cheat.impl.player;

import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatAllowedCharacters;
import us.nebula.client.Nebula;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.setting.Setting;
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

    private final Setting<File> spammerFileSetting = builder("File", SPAMMER_DIRECTORY)
            .setDescription("The file to read the spam text from")
            .onValueChanged((value) -> readSpammerFile())
            .build();
    private final Setting<Mode> modeSetting = enumBuilder("Mode", Mode.LOOP)
            .setDescription("The mode to spam the chat with")
            .build();
    private final Setting<Double> delaySetting = numberBuilder("Delay", 1.5)
            .setMin(0.0)
            .setMax(20.0)
            .setScale(0.1)
            .setDescription("The delay in seconds before sending the next message")
            .build();

    private final List<String> spammerLines = new LinkedList<>();
    private int spammerIndex = 0;
    private final Timer timer = new Timer();

    @Override
    public void onEnable()
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
            notifyError("Spammer file either does not exist, or was not set", 5000L);
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
