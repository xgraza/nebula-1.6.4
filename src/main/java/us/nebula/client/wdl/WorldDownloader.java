package us.nebula.client.wdl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.nebula.client.util.player.ChatUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author xgraza
 * @since 5/10/26
 */
public final class WorldDownloader
{
    private static final Logger LOGGER = LogManager.getLogger("WDL");
    private static final Minecraft MC = Minecraft.getMinecraft();
    public static final WorldDownloader INSTANCE = new WorldDownloader();

    private Thread downloadThread;
    private final List<Chunk> chunksToDownloadList = new CopyOnWriteArrayList<>();
    private boolean alwaysDownload;

    private File savesFolder;
    private SaveHandler saveHandler;

    private WorldDownloader()
    {
        if (INSTANCE != null)
        {
            throw new RuntimeException("");
        }
    }

    public void start()
    {
        if (isDownloading())
        {
            return;
        }

        downloadThread = new Thread(() ->
        {
            send("Creating save files...");
            savesFolder = createNewSaveFolder();
            saveHandler = (SaveHandler) MC.getSaveLoader().getSaveLoader(getSaveName(), true);

            send("Saving level data");
            saveLevelData(getLevelNBT());
            send("Saving player data");
            savePlayerData(getPlayerNBT());
            send("Saving chunk data");
            saveChunks();

            final IChunkLoader chunkLoader = saveHandler.getChunkLoader(MC.theWorld.provider);
            while (alwaysDownload)
            {
                for (final Chunk chunk : chunksToDownloadList)
                {
                    saveChunk(chunkLoader, chunk);
                    chunksToDownloadList.remove(chunk);
                }
            }

            finishDownload();
        });
        downloadThread.start();
    }

    private void finishDownload()
    {
        downloadThread.interrupt();
        downloadThread = null;

        send("Writing to disk");
        MC.getSaveLoader().flushCache();
        saveHandler.flush();

        alwaysDownload = false;
        send("Done! World name is \"%s\"", getSaveName());
        LOGGER.info("Finished downloading!");
    }

    public void stop()
    {
        if (!isDownloading())
        {
            return;
        }
        finishDownload();
    }

    public boolean isDownloading()
    {
        return downloadThread != null;
    }

    public void addChunk(final Chunk chunk)
    {
        if (chunksToDownloadList.contains(chunk) || !isDownloading() || !alwaysDownload)
        {
            return;
        }
        chunksToDownloadList.add(chunk);
    }

    public void setAlwaysDownload(boolean alwaysDownload)
    {
        LOGGER.info("Always download: {}", alwaysDownload);
        this.alwaysDownload = alwaysDownload;
    }

    private void send(final String message, final Object... format)
    {
        ChatUtil.sendFormatted(ChatUtil.WORLD_DOWNLOADER_PREFIX, message, format);
    }

    private NBTTagCompound getPlayerNBT()
    {
        final NBTTagCompound compound = new NBTTagCompound();
        MC.thePlayer.writeToNBT(compound);
        return compound;
    }

    private void savePlayerData(final NBTTagCompound compound)
    {
        final File directory = new File(savesFolder, "players");
        if (!directory.exists() || !directory.isDirectory())
        {
            if (!directory.mkdir())
            {
                LOGGER.error("Could not make directory {}", directory);
                return;
            }
        }
        final File playerDataFile = new File(directory, MC.getSession().getUsername() + ".dat");
        if (!playerDataFile.exists() || !playerDataFile.isFile())
        {
            try
            {
                playerDataFile.createNewFile();
            } catch (IOException e)
            {
                LOGGER.error("Failed to create player data file \"{}\"!", playerDataFile);
                throw new RuntimeException(e);
            }
        }
        try (final OutputStream os = Files.newOutputStream(playerDataFile.toPath()))
        {
            CompressedStreamTools.writeCompressed(compound, os);
        } catch (final IOException e)
        {
            LOGGER.error("Failed to write compressed player NBT data to \"{}\"!", playerDataFile);
            throw new RuntimeException(e);
        }
        LOGGER.info("Wrote {} bytes to \"{}\"", compound.getSize(false), playerDataFile);
    }

    private NBTTagCompound getLevelNBT()
    {
        final NBTTagCompound compound = MC.theWorld.getWorldInfo().cloneNBTCompound(getPlayerNBT());
        compound.setString("LevelName", getSaveName());
        compound.setByte("allowCommands", (byte) 1);
        compound.setByte("initialized", (byte) 1);
        compound.setString("generatorName", "flat");
        compound.setInteger("generatorVersion", 0);
        compound.setBoolean("MapFeatures", false);
        compound.setInteger("version", ((AnvilSaveConverter) MC.getSaveLoader()).getSaveVersion());
        return compound;
    }

    private void saveLevelData(final NBTTagCompound worldCompound)
    {
        final File levelFile = new File(savesFolder, "level.dat");
        if (!levelFile.exists())
        {
            try
            {
                levelFile.createNewFile();
            } catch (IOException e)
            {
                LOGGER.error("Failed to create level data file \"{}\"!", levelFile);
                throw new RuntimeException(e);
            }
        }
        final NBTTagCompound data = new NBTTagCompound();
        data.setTag("Data", worldCompound);
        try (final OutputStream os = Files.newOutputStream(levelFile.toPath()))
        {
            CompressedStreamTools.writeCompressed(data, os);
        } catch (final IOException e)
        {
            LOGGER.error("Failed to write compressed NBT data to \"{}\"!", levelFile);
            throw new RuntimeException(e);
        }
        LOGGER.info("Wrote {} bytes to \"{}\"", data.getSize(false), levelFile);
    }

    private void saveChunks()
    {
        try
        {
            saveHandler.checkSessionLock();
        } catch (final MinecraftException e)
        {
            LOGGER.error("Failed checkSessionLock!", e);
            return;
        }

        LOGGER.info("Saving loaded chunks");

        final IChunkLoader chunkLoader = saveHandler.getChunkLoader(MC.theWorld.provider);
        final ChunkProviderClient chunkProvider = (ChunkProviderClient) MC.theWorld.getChunkProvider();
        final LongHashMap.Entry[] entries = chunkProvider.getChunkMapping().getHashArray();
        if (entries.length == 0)
        {
            LOGGER.error("No chunk data!");
            return;
        }

        for (final LongHashMap.Entry entry : entries)
        {
            if (entry == null || !(entry.getValue() instanceof Chunk))
            {
                continue;
            }
            saveChunk(chunkLoader, (Chunk) entry.getValue());
        }
    }

    private void saveChunk(final IChunkLoader chunkLoader, final Chunk chunk)
    {
        if (chunk == null || !chunk.isModified)
        {
            return;
        }
        chunk.isTerrainPopulated = true;
        try
        {
            chunkLoader.saveChunk(MC.theWorld, chunk);
            ThreadedFileIOBase.threadedIOInstance.waitForFinish();
            // send("Saved chunk @ %s/%s", chunk.xPosition, chunk.zPosition);
        } catch (final MinecraftException | IOException | InterruptedException e)
        {
            LOGGER.error("Failed to save chunk!", e);
        }
    }

    private File createNewSaveFolder()
    {
        final File file = new File(MC.mcDataDir, "/saves/" + getSaveName());
        if (file.exists())
        {
            file.delete();
        }
        if (!file.exists() || !file.isDirectory())
        {
            file.mkdir();
        }
        return file;
    }

    private String getSaveName()
    {
        final ServerData serverData = MC.getCurrentServerData();
        if (serverData != null && serverData.serverIP != null)
        {
            String serverName = serverData.serverIP.trim();
            final String[] parts = serverName.split(":");
            if (parts.length != 1)
            {
                serverName = parts[0];
            }
            return serverName.isEmpty()
                    ? "UnknownServerName"
                    : serverName.replaceAll("\\W+", "_");
        }
        return "UnknownServerName";
    }
}
