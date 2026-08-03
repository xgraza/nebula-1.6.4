package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.setting.EnumSetting;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.util.minecraft.network.PacketUtil;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatAllowedCharacters;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.io.FileUtil;
import ez.nebula.client.util.math.MathUtil;
import ez.nebula.client.util.math.Timer;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 03/25/25
 */
@ModuleManifest(name = "Spammer",
        description = "Spams things in chat",
        category = ModuleCategory.PLAYER)
public final class SpammerModule extends Module
{
    public static final File SPAMMER_DIRECTORY = new File(
            Nebula.INSTANCE.getNebulaRootDir(), "spammer");

    private final List<String> spammerLines = new LinkedList<>();
    private int spammerIndex = 0;
    private final Timer timer = new Timer();

    private final Setting<File> spammerFileSetting = builder("File", SPAMMER_DIRECTORY)
            .setDescription("The file to read the spam text from")
            .onValueChanged(this::readSpammerFile)
            .build();
    private final EnumSetting<Mode> modeSetting = enumBuilder("Mode", Mode.LOOP)
            .setDescription("The mode to spam the chat with")
            .build();
    private final NumberSetting<Double> delaySetting = numberBuilder("Delay", 1.5)
            .setMin(0.0)
            .setMax(20.0)
            .setScale(0.1)
            .setDescription("The delay in seconds before sending the next message")
            .build();

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

        if (spammerLines.isEmpty())
        {
            readSpammerFile(spammerFileSetting.getValue());
        }
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
                    PacketUtil.send(new C01PacketChatMessage(line));
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
                    PacketUtil.send(new C01PacketChatMessage(
                            spammerLines.get(MathUtil.random(0, spammerLines.size() - 1))));
                    break;
                }
            }
            timer.resetTime();
        }
    };

    private void readSpammerFile(final File file)
    {
        spammerLines.clear();
        spammerIndex = 0;
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
