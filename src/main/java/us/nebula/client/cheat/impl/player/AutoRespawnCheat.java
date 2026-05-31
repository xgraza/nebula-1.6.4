package us.nebula.client.cheat.impl.player;

import net.minecraft.network.play.client.C16PacketClientStatus;
import us.nebula.client.Nebula;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.network.EventDisconnect;
import us.nebula.client.listener.event.player.EventPlayerDeath;
import us.nebula.client.setting.Setting;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
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
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

    private final Setting<Boolean> logCoordsSetting = builder("Log Coordinates", false)
            .setDescription("If to log death coordinates to a file")
            .build();

    private OutputStream fileStream;

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (fileStream == null)
        {
            return;
        }
        closeCoordFileStream();
    }

    @Subscribe
    private final EventListener<EventPlayerDeath> playerDeathEventListener = event ->
    {
        if (event.getPlayer().equals(MC.thePlayer))
        {
            if (logCoordsSetting.getValue())
            {
                final String format = String.format("%.2f, %.2f, %.2f", MC.thePlayer.posX, MC.thePlayer.boundingBox.minY, MC.thePlayer.posZ);
                writeCoordsToFile(format);
                notifyInfo("You died at XYZ: " + format, 10000L);
            }
            MC.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(
                    C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
        }
    };

    @Subscribe
    private final EventListener<EventDisconnect> disconnectEventListener = event ->
            closeCoordFileStream();

    private boolean createCoordFileStream()
    {
        if (fileStream != null)
        {
            closeCoordFileStream();
        }

        final File coordinateLogFolder = new File(Nebula.INSTANCE.getNebulaRootDir(), "respawn_coords");
        if (!coordinateLogFolder.exists())
        {
            if (!coordinateLogFolder.mkdir())
            {
                notifyError("Failed to create parent directory", 7500L);
                return false;
            }
        }

        // System.out.println(Nebula.INSTANCE.getServerManager().getServerIP());
        final File file = new File(coordinateLogFolder,
                Nebula.INSTANCE.getServerManager().getServerIP() + ".txt");
        try
        {
            if (!file.exists() && !file.createNewFile())
            {
                notifyError("Failed to create file", 7500L);
                return false;
            }
            fileStream = Files.newOutputStream(file.toPath(), StandardOpenOption.APPEND);
            Runtime.getRuntime().addShutdownHook(new Thread(this::closeCoordFileStream,
                    "AutoRespawn-File-Writer"));
        } catch (final IOException e)
        {
            notifyError("Failed to open file stream", 7500L);
        }
        return true;
    }

    private void closeCoordFileStream()
    {
        // System.out.println("Closing file stream");
        if (fileStream != null)
        {
            try
            {
                fileStream.flush();
                fileStream.close();
            } catch (final IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        fileStream = null;
    }

    private void writeCoordsToFile(final String coordinates)
    {
        if (fileStream == null && !createCoordFileStream())
        {
            return;
        }
        final String writeStr = TIME_FORMAT.format(new Date()) + " -> " + coordinates + " (" + MC.thePlayer.getCommandSenderName() + ")\n";
        final byte[] bytes = writeStr.getBytes(StandardCharsets.UTF_8);
        try
        {
            fileStream.write(bytes, 0, bytes.length);
            fileStream.flush();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
