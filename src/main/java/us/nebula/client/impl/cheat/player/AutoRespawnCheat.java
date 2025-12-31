package us.nebula.client.impl.cheat.player;

import net.minecraft.network.play.client.C16PacketClientStatus;
import us.nebula.client.Nebula;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.player.EventPlayerDeath;
import us.nebula.client.util.player.ChatUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CheatManifest(name = "AutoRespawn",
        description = "Automatically respawns & retains coordinates of death",
        category = CheatCategory.PLAYER)
public final class AutoRespawnCheat extends Cheat
{
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm:ss");
    private static final File COORDINATE_SAVE_FILE;
    private static OutputStream OPEN_FILE_STREAM;

    static
    {
        final File coordinateLogFolder = new File(Nebula.INSTANCE.getNebulaRootDir(), "respawn_coords");
        if (!coordinateLogFolder.exists())
        {
            if (!coordinateLogFolder.mkdir())
            {
                throw new RuntimeException("Failed to create " + coordinateLogFolder);
            }
        }

        int index = 0;
        File file = null;
        while (file == null)
        {
            String name = "coordinates_" + DATE_FORMAT.format(new Date());
            if (index > 0)
            {
                name += "_session_" + index;
            }
            final File logFile = new File(coordinateLogFolder, name);
            if (logFile.exists())
            {
                ++index;
                continue;
            }
            file = logFile;
        }

        COORDINATE_SAVE_FILE = file;
        try
        {
            COORDINATE_SAVE_FILE.createNewFile();
            OPEN_FILE_STREAM = Files.newOutputStream(COORDINATE_SAVE_FILE.toPath());

            Runtime.getRuntime().addShutdownHook(new Thread(() ->
            {
                if (OPEN_FILE_STREAM != null)
                {
                    try
                    {
                        OPEN_FILE_STREAM.flush();
                        OPEN_FILE_STREAM.close();
                    } catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }, "AutoRespawn-File-Writer"));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private final Setting<Boolean> logCoordsSetting = new Setting<>("Log Coordinates", false);

    @Subscribe
    private final EventListener<EventPlayerDeath> playerDeathEventListener = event ->
    {
        if (event.getPlayer().equals(MC.thePlayer))
        {
            if (logCoordsSetting.getValue())
            {
                writeCoordsToFile(String.format("%.2f, %.2f, %.2f", MC.thePlayer.posX, MC.thePlayer.boundingBox.minY, MC.thePlayer.posZ));
                ChatUtil.send("Writing coordinates to file...");
            }
            MC.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(
                    C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
        }
    };

    private void writeCoordsToFile(final String coordinates)
    {
        if (OPEN_FILE_STREAM == null)
        {
            return;
        }
        final String writeStr = TIME_FORMAT.format(new Date()) + " -> " + coordinates + "\n";
        final byte[] bytes = writeStr.getBytes(StandardCharsets.UTF_8);
        try
        {
            OPEN_FILE_STREAM.write(bytes, 0, bytes.length);
            OPEN_FILE_STREAM.flush();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
