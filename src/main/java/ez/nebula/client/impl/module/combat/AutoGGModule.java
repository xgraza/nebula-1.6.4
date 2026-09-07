package ez.nebula.client.impl.module.combat;

import com.google.common.collect.Lists;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.listener.event.player.EventPlayerDeath;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.io.FileUtil;
import ez.nebula.client.util.io.SoundUtil;
import ez.nebula.client.util.math.MathUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xgraza
 * @since 8/7/26
 */
@ModuleManifest(name = "AutoGG",
        description = "Automatically sends good wishes towards your targets' way",
        category = ModuleCategory.COMBAT)
public final class AutoGGModule extends Module
{
    private static final Logger LOGGER = LogManager.getLogger("AutoGG");

    @ModuleInstance
    public static AutoGGModule INSTANCE;

    private static final File AUTO_GG_FILE = new File(Nebula.NEBULA_ROOT, "auto_gg.txt");
    private static final String PLAYER_KEY = "%player%";
    private static final List<String> DEFAULT_AUTO_GG = Lists.newArrayList(
            "# Create a new line for each message. Use the key " + PLAYER_KEY + " for the player's username.",
            "Good game, " + PLAYER_KEY + "!",
            "Nice fight, " + PLAYER_KEY + "!");

    static
    {
        if (!AUTO_GG_FILE.exists())
        {
            try
            {
                if (!AUTO_GG_FILE.createNewFile())
                {
                    LOGGER.warn("Could not create {}", AUTO_GG_FILE);
                }
                LOGGER.info("Saving default AutoGG contents to {}",  AUTO_GG_FILE);
                // save default data
                FileUtil.save(AUTO_GG_FILE, String.join("\n", DEFAULT_AUTO_GG));
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private final Setting<Boolean> friendsSetting = builder("Friends", false)
            .setDescription("If to also send a message to your friends when they die")
            .build();
    private final Setting<Boolean> vineBoomSetting = builder("Vine Boom", false)
            .setDescription("If to play a vine boom")
            .build();

    private final List<String> autoGGLineList = new ArrayList<>();

    private EntityPlayer lastTarget;

    @Override
    public void onEnable()
    {
        super.onEnable();
        lastTarget = null;
        autoGGLineList.clear();
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (autoGGLineList.isEmpty())
        {
            try
            {
                readAutoGGFile();
            } catch (final IOException e)
            {
                LOGGER.error("Failed to read auto_gg.txt file", e);
                toggle();
            }
        }
    };

    @Subscribe
    private final EventListener<EventPlayerDeath> playerDeathEventListener = event ->
    {
        if (MC.thePlayer == null || MC.theWorld == null)
        {
            return;
        }
        if (lastTarget != null && lastTarget.equals(event.getPlayer()) && !MC.thePlayer.equals(event.getPlayer()))
        {
            if (!friendsSetting.getValue() && Nebula.FRIENDS.has(event.getPlayer()))
            {
                return;
            }

            String message = getRandomMessage();
            if (message == null || message.isEmpty())
            {
                return;
            }
            message = message.replaceAll(PLAYER_KEY, lastTarget.getCommandSenderName());
            lastTarget = null;
            MC.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(message));

            if (vineBoomSetting.getValue())
            {
                SoundUtil.vineBoom();
            }
        }
    };

    private String getRandomMessage()
    {
        if (autoGGLineList.isEmpty())
        {
            return null;
        }
        if (autoGGLineList.size() == 1)
        {
            return autoGGLineList.get(0);
        }
        return autoGGLineList.get(MathUtil.random(0, autoGGLineList.size() - 1));
    }

    private void readAutoGGFile() throws IOException
    {
        if (!AUTO_GG_FILE.exists() || !AUTO_GG_FILE.isFile() || !AUTO_GG_FILE.canRead())
        {
            notifyError("The auto_gg.txt file could not be read/accessed.", 7500L);
            toggle();
            return;
        }
        final String content = FileUtil.read(AUTO_GG_FILE);
        if (content.isEmpty())
        {
            notifyWarn("Your auto_gg.txt file is empty!", 7500L);
            toggle();
            return;
        }
        autoGGLineList.clear();
        final String[] lines = content.split("\n");
        for (String line : lines)
        {
            line = line.trim();
            if (line.startsWith("#"))
            {
                continue;
            }
            autoGGLineList.add(line);
        }
        if (autoGGLineList.isEmpty())
        {
            notifyWarn("Your auto_gg.txt file is empty!", 7500L);
            toggle();
        }
    }

    public void setLastTarget(EntityPlayer lastTarget)
    {
        this.lastTarget = lastTarget;
    }
}
