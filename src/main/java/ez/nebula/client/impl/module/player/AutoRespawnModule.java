package ez.nebula.client.impl.module.player;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.util.minecraft.network.PacketUtil;
import net.minecraft.network.play.client.C16PacketClientStatus;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.network.EventDisconnect;
import ez.nebula.client.api.listener.event.player.EventPlayerDeath;
import ez.nebula.client.api.setting.Setting;

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
@ModuleManifest(name = "AutoRespawn",
        description = "Automatically respawns & retains coordinates of death",
        category = ModuleCategory.PLAYER)
public final class AutoRespawnModule extends Module
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
            PacketUtil.send(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
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

        final File coordinateLogFolder = new File(Nebula.NEBULA_ROOT, "respawn_coords");
        if (!coordinateLogFolder.exists())
        {
            if (!coordinateLogFolder.mkdir())
            {
                notifyError("Failed to create parent directory", 7500L);
                return false;
            }
        }

        // System.out.println(Nebula2.SERVER.getServerIP());
        final File file = new File(coordinateLogFolder,
                Nebula.SERVER.ip() + ".txt");
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
